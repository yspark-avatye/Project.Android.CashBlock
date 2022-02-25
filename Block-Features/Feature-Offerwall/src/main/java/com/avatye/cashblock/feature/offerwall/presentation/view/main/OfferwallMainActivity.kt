package com.avatye.cashblock.feature.offerwall.presentation.view.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.avatye.cashblock.base.component.contract.business.CoreContractor
import com.avatye.cashblock.base.component.contract.business.ViewOpenContractor
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallJourneyStateType
import com.avatye.cashblock.base.component.domain.model.parcel.ServiceNameParcel
import com.avatye.cashblock.base.component.support.launch
import com.avatye.cashblock.base.component.widget.banner.BannerLinearView
import com.avatye.cashblock.feature.offerwall.OfferwallConfig.logger
import com.avatye.cashblock.feature.offerwall.component.controller.ADController
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoActivityOfferwallMainBinding
import com.avatye.cashblock.feature.offerwall.presentation.AppBaseActivity
import com.avatye.cashblock.feature.offerwall.presentation.parcel.OfferWallActionParcel
import com.avatye.cashblock.feature.offerwall.presentation.view.detail.OfferwallDetailViewActivity
import com.avatye.cashblock.feature.offerwall.presentation.view.setting.SettingActivity

internal class OfferwallMainActivity : AppBaseActivity() {

    lateinit var offerwallMainFragment: OfferwallMainFragment

    companion object {
        fun open(
            activity: Activity,
            serviceType: ServiceType,
            close: Boolean = false
        ) {
            activity.launch(
                intent = Intent(activity, OfferwallMainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra(ServiceNameParcel.NAME, ServiceNameParcel(serviceType = serviceType))
                },
                transition = ActivityTransitionType.NONE.value,
                close = close
            )
        }
    }

    private val vb: AcbsoActivityOfferwallMainBinding by lazy {
        AcbsoActivityOfferwallMainBinding.inflate(LayoutInflater.from(this))
    }

    override fun receiveActionInspection() {
        leakHandler.post {
            ViewOpenContractor.openInspectionView(
                activity = this@OfferwallMainActivity,
                blockType = getBlockType(),
                close = true
            )
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }

        if (requestCode == OfferwallDetailViewActivity.REQUEST_CODE) {
            data?.extras?.getParcelable<OfferWallActionParcel>(OfferWallActionParcel.NAME)?.let {
                if (it.journeyType == OfferwallJourneyStateType.NONE) {
                    if (it.forceRefresh) {
                        offerwallMainFragment.requestOfferWallList()
                    }
                } else {
                    offerwallMainFragment.offerwallListPagerAdapter.changeAllList(it.currentPosition, it.journeyType)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(vb.root)
        // region { banner }
        vb.bannerLinearView.bannerData = ADController.createBannerData()
        vb.bannerLinearView.sourceType = BannerLinearView.SourceType.OFFERWALL
        vb.bannerLinearView.requestBanner()
        // endregion
        with(vb.headerView) {
            actionMore { SettingActivity.open(activity = this@OfferwallMainActivity, serviceType = serviceType, close = false) }
            actionClose { finish() }
        }
        // region # deviceAAID
        CoreContractor.DeviceSetting.fetchAAID {
            logger.i(viewName = viewTag) {
                "CoreContractor.DeviceSetting.fetchAAID { complete: $it }"
            }
        }
        // endregion
        // transaction fragment
        offerwallMainFragment = OfferwallMainFragment()
        transactionFragment()
        // endregion
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


    private fun transactionFragment() {
        supportFragmentManager.beginTransaction().apply {
            replace(vb.offerwallListContainer.id, offerwallMainFragment)
            commit()
        }
    }

}