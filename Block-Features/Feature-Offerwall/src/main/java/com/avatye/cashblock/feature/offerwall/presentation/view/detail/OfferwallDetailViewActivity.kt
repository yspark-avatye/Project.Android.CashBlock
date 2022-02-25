package com.avatye.cashblock.feature.offerwall.presentation.view.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.avatye.cashblock.base.component.contract.api.OfferwallApiContractor
import com.avatye.cashblock.base.component.contract.business.CoreContractor
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallImpressionItemEntity
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallJourneyStateType
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.domain.model.parcel.ServiceNameParcel
import com.avatye.cashblock.base.component.support.CoreUtil
import com.avatye.cashblock.base.component.support.MessageDialogHelper
import com.avatye.cashblock.base.component.support.extraParcel
import com.avatye.cashblock.base.component.support.launchFortResult
import com.avatye.cashblock.base.component.widget.banner.BannerLinearView
import com.avatye.cashblock.feature.offerwall.OfferwallConfig
import com.avatye.cashblock.feature.offerwall.R
import com.avatye.cashblock.feature.offerwall.component.controller.ADController
import com.avatye.cashblock.feature.offerwall.component.controller.AdvertiseController
import com.avatye.cashblock.feature.offerwall.component.data.PreferenceData
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoActivityOfferwallDetailViewBinding
import com.avatye.cashblock.feature.offerwall.presentation.AppBaseActivity
import com.avatye.cashblock.feature.offerwall.presentation.parcel.OfferWallActionParcel
import com.avatye.cashblock.feature.offerwall.presentation.parcel.OfferWallViewParcel

internal class OfferwallDetailViewActivity : AppBaseActivity(), View.OnClickListener {

    companion object {
        /** this activity code */
        const val REQUEST_CODE = 11001
        const val MAX_HIDDEN_ITEM: Int = 1000

        fun open(activity: Activity, serviceType: ServiceType, parcel: OfferWallViewParcel, options: Bundle?) {
            activity.launchFortResult(
                intent = Intent(activity, OfferwallDetailViewActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra(OfferWallViewParcel.NAME, parcel)
                    putExtra(ServiceNameParcel.NAME, ServiceNameParcel(serviceType = serviceType))
                },
                transition = ActivityTransitionType.NONE.value,
                requestCode = REQUEST_CODE,
                options = options,
            )
        }
    }

    private val api: OfferwallApiContractor by lazy {
        OfferwallApiContractor(blockType = OfferwallConfig.blockType)
    }

    private var parcel: OfferWallViewParcel? = null


    private val vb: AcbsoActivityOfferwallDetailViewBinding by lazy {
        AcbsoActivityOfferwallDetailViewBinding.inflate(LayoutInflater.from(this))
    }

    var advertiseStatus: OfferwallJourneyStateType = OfferwallJourneyStateType.NONE
        set(value) {
            field = value
            when (field) {
                OfferwallJourneyStateType.PARTICIPATE,
                OfferwallJourneyStateType.COMPLETED_NOT_REWARDED -> {
                    vb.adHide.isVisible = false
                    vb.adClose.isVisible = true
                    vb.adRewardInquiry.isVisible = true
                }
                else -> {
                    vb.adHide.isVisible = true
                    vb.adClose.isVisible = false
                    vb.adRewardInquiry.isVisible = false
                }
            }
        }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onBackPressed() {
        supportFinishAfterTransition()
    }

    override fun onResume() {
        super.onResume()
        // banners
        vb.bannerLinearView.onResume()
    }

    override fun onPause() {
        super.onPause()
        // banners
        vb.bannerLinearView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // banner
        vb.bannerLinearView.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(vb.root)
        parcel = extraParcel(OfferWallViewParcel.NAME)
        // header
        vb.headerView.actionBack { finish() }
        // onClick
        vb.adRewardInquiry.setOnClickListener(this)
        vb.adClose.setOnClickListener(this)
        vb.adHide.setOnClickListener(this)
        transactionFragment(fragment = OfferwallDetailViewFragment().apply {
            val bundle = Bundle()
            bundle.putParcelable(OfferWallViewParcel.NAME, this@OfferwallDetailViewActivity.parcel)
            arguments = bundle
        })
        // region { banner }
        vb.bannerLinearView.bannerData = ADController.createBannerData()
        vb.bannerLinearView.sourceType = BannerLinearView.SourceType.OFFERWALL
        vb.bannerLinearView.requestBanner()
        // endregion
    }


    private fun transactionFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.ad_detail_view_container, fragment)
            commit()
        }
    }


    private fun requestOfferWallClose() {
        loadingView?.show(false)
        CoreContractor.DeviceSetting.retrieveAAID { aaidEntity ->
            api.postClose(
                deviceADID = aaidEntity.aaid,
                advertiseID = parcel?.advertiseID ?: "",
            ) {
                when (it) {
                    is ContractResult.Success -> {
                        loadingView?.dismiss()
                        setResult(
                            Activity.RESULT_OK, Intent().apply {
                                putExtra(
                                    OfferWallActionParcel.NAME,
                                    OfferWallActionParcel(
                                        currentPosition = parcel?.currentPos ?: 0,
                                        journeyType = OfferwallJourneyStateType.COMPLETED_FAILED,
                                    )
                                )
                            }
                        )
                        finish()
                    }
                    is ContractResult.Failure -> {
                        loadingView?.dismiss()
                        CoreUtil.showToast(R.string.acbso_offerwall_failed_to_remove_please_try_again)
                    }
                }
            }
        }
    }


    private fun startRewardInquiry(entity: OfferwallImpressionItemEntity) {
    }


    fun startCloseAdvertise() = requestOfferWallClose()


    private fun startHideAdvertise(entity: OfferwallImpressionItemEntity) {
        val hiddenItems: MutableList<String>? = PreferenceData.Hidden.hiddenItems?.toMutableList()
        hiddenItems?.let {
            if (!it.contains(entity.advertiseID)) {
                if (it.size >= MAX_HIDDEN_ITEM) {
                    it.removeFirst()
                }

                val resultItem = it.plus(entity.advertiseID)
                PreferenceData.Hidden.update(hiddenItems = resultItem)

                val actionParcel = parcel?.let { parcel ->
                    OfferWallActionParcel(
                        currentPosition = parcel.currentPos,
                        journeyType = OfferwallJourneyStateType.NONE,
                        forceRefresh = true
                    )
                }
                setResult(RESULT_OK, Intent().apply {
                    putExtra(OfferWallActionParcel.NAME, actionParcel)
                })
                finish()
            }
        }
    }


    override fun onClick(v: View?) {
        val impressionItemEntity = AdvertiseController.impressionItemEntity

        when (v?.id) {
            vb.adRewardInquiry.id -> {
                startRewardInquiry(impressionItemEntity)
            }

            vb.adClose.id -> {
                MessageDialogHelper.determine(
                    activity = this@OfferwallDetailViewActivity,
                    message = R.string.acbso_offerwall_do_you_want_to_remove_from_the_participating_list,
                    onPositive = {
                        startCloseAdvertise()
                    }
                ).show(false)
            }

            vb.adHide.id -> {
                MessageDialogHelper.determine(
                    activity = this@OfferwallDetailViewActivity,
                    message = R.string.acbso_offerwall_do_you_want_to_hide_from_the_participating_list,
                    onPositive = {
                        startHideAdvertise(impressionItemEntity)
                    }
                ).show(false)
            }
        }
    }
}