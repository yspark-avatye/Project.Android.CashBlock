package com.avatye.cashblock.feature.offerwall.presentation.view.detail

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import com.avatye.cashblock.base.component.contract.business.CoreContractor
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallImpressionItemEntity
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallJourneyStateType
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallProductType
import com.avatye.cashblock.base.component.domain.model.parcel.ServiceNameParcel
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult
import com.avatye.cashblock.base.component.support.*
import com.avatye.cashblock.base.component.widget.banner.BannerLinearView
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.feature.offerwall.R
import com.avatye.cashblock.feature.offerwall.component.controller.ADController
import com.avatye.cashblock.feature.offerwall.component.controller.AdvertiseController
import com.avatye.cashblock.feature.offerwall.component.data.PreferenceData
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoActivityOfferwallDetailViewBinding
import com.avatye.cashblock.feature.offerwall.presentation.AppBaseActivity
import com.avatye.cashblock.feature.offerwall.presentation.parcel.InquiryParcel
import com.avatye.cashblock.feature.offerwall.presentation.parcel.OfferWallActionParcel
import com.avatye.cashblock.feature.offerwall.presentation.parcel.OfferWallViewParcel
import com.avatye.cashblock.feature.offerwall.presentation.view.inquiry.InquiryRewardActivity
import com.avatye.cashblock.feature.offerwall.presentation.viewmodel.detail.OfferwallDetailViewModel
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import org.joda.time.DateTime

internal class OfferwallDetailViewActivity : AppBaseActivity(), View.OnClickListener {

    private var isRequestConfiirm: Boolean = false

    companion object {
        /** this activity code */
        const val REQUEST_CODE = 11001
        const val MAX_HIDDEN_ITEM: Int = 1000
        const val tagName: String = "OfferwallDetailViewActivity"

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

    private val viewModel by lazy {
        OfferwallDetailViewModel.create(this)
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

    override fun onBackPressed() {
        supportFinishAfterTransition()
    }

    override fun onResume() {
        super.onResume()
        // banners
        vb.bannerLinearView.onResume()
        if (isRequestConfiirm) {
            isRequestConfiirm = false
            requestImpression()
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }
        if (requestCode == InquiryRewardActivity.REQUEST_CODE) {
            requestImpression()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(vb.root)
        parcel = extraParcel(OfferWallViewParcel.NAME)
        // header
        vb.headerView.actionBack { finish() }
        // banner
        vb.bannerLinearView.bannerData = ADController.createBannerData()
        vb.bannerLinearView.sourceType = BannerLinearView.SourceType.OFFERWALL
        vb.bannerLinearView.requestBanner()
        // onClick
        vb.adRewardInquiry.setOnClickListener(this)
        vb.adClose.setOnClickListener(this)
        vb.adHide.setOnClickListener(this)
        // observe
        observeViewModel()
    }


    private fun observeViewModel() {
        // region # impression
        viewModel.impressionResult.observe(this) {
            when (it) {
                is ViewModelResult.InProgress -> loadingView?.show(cancelable = false)
                is ViewModelResult.Error -> {
                    loadingView?.dismiss()
                    MessageDialogHelper.confirm(
                        activity = this,
                        message = getString(R.string.acb_common_message_error),
                        onConfirm = { finish() }
                    ).show(false)
                }
                is ViewModelResult.Complete -> {
                    loadingView?.dismiss()
                    AdvertiseController.impressionItemEntity = it.result

                    // delay
                    Handler(Looper.getMainLooper()).postDelayed({
                        loadingView?.dismiss()
                    }, 1000)
                    // bindView
                    bindView(it.result)
                }
            }
        }
        requestImpression()
        // endregion

        // region # confirm
        viewModel.clickResult.observe(this) {
            when (it) {
                is ViewModelResult.InProgress -> loadingView?.show(false)
                is ViewModelResult.Error -> {
                    loadingView?.dismiss()
                    MessageDialogHelper.confirm(
                        activity = this,
                        message = it.message,
                        onConfirm = { actionCloseAdvertise() }
                    ).show(false)

                    // error case
//                    if (it.errorCode.equals(other = "err_invalid_parameter", ignoreCase = true)
//                        || it.errorCode.equals(other = "err_not_support_network", ignoreCase = true)
//                        || it.errorCode.equals(other = "err_not_exists_advertise", ignoreCase = true)
//                        || it.errorCode.equals(other = "err_fail_click_already", ignoreCase = true)
//                        || it.errorCode.equals(other = "err_fail_click_invalid", ignoreCase = true)
//                        || it.errorCode.equals(other = "err_fail_click_closed", ignoreCase = true)
//                        || it.errorCode.equals(other = "err_fail_click_not_exists", ignoreCase = true)
//                        || it.errorCode.equals(other = "err_fail_click_exhausted", ignoreCase = true)
//                        || it.errorCode.equals(other = "err_fail_click_not_target", ignoreCase = true)
//                    )

                }
                is ViewModelResult.Complete -> {
                    runCatching {
                        val actionParcel = OfferWallActionParcel(
                            currentPosition = parcel?.currentPos ?: 0,
                            journeyType = OfferwallJourneyStateType.PARTICIPATE,
                            forceRefresh = false
                        )
                        setResult(Activity.RESULT_OK, Intent().apply { putExtra(OfferWallActionParcel.NAME, actionParcel) })
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.result.landingUrl)))
                    }.onSuccess {
                        isRequestConfiirm = true
                    }.onFailure { e ->
                        e.printStackTrace()
                        isRequestConfiirm = false
                        MessageDialogHelper.confirm(
                            activity = this,
                            message = R.string.acbso_offerwall_failed_to_participate_please_try_again,
                            onConfirm = { finish() }
                        ).show(false)
                    }.also {
                        loadingView?.dismiss()
                    }
                }
            }
        }
        // endregion

        // region # conversion
        viewModel.conversionResult.observe(this) {
            when (it) {
                is ViewModelResult.InProgress -> loadingView?.show(false)
                is ViewModelResult.Error -> {
                    loadingView?.dismiss()
                    MessageDialogHelper.confirm(
                        activity = this,
                        message = getString(R.string.acb_common_message_error),
                        onConfirm = { finish() }
                    ).show(false)
                }
                is ViewModelResult.Complete -> {
                    loadingView?.dismiss()
                    MessageDialogHelper.confirm(
                        activity = this,
                        message = R.string.acbso_offerwall_cash_has_been_earned_please_check_your_accumulation_details,
                        onConfirm = {
                            val intent: Intent = Intent().apply {
                                putExtra(
                                    OfferWallActionParcel.NAME, OfferWallActionParcel(
                                        currentPosition = parcel?.currentPos ?: 0,
                                        journeyType = OfferwallJourneyStateType.COMPLETED_REWARDED,
                                        forceRefresh = false
                                    )
                                )
                            }
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    ).show(false)
                }
            }
        }
        // endregion

        // region # close
        viewModel.closeResult.observe(this) {
            when (it) {
                is ViewModelResult.InProgress -> loadingView?.show(false)
                is ViewModelResult.Error -> {
                    loadingView?.dismiss()
                    CoreUtil.showToast(R.string.acbso_offerwall_failed_to_remove_please_try_again)
                }
                is ViewModelResult.Complete -> {
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
            }
        }
        // endregion
    }


    private fun requestImpression() {
        CoreContractor.DeviceSetting.retrieveAAID { aaidEntity ->
            viewModel.requestImpression(
                deviceADID = aaidEntity.aaid,
                advertiseID = parcel?.advertiseID ?: "",
                service = serviceType,
            )
        }
    }


    private fun bindView(impressionItem: OfferwallImpressionItemEntity) {
        with(impressionItem) {
            // region # icon
            glider?.let {
                it.load(iconUrl)
                    .apply(RequestOptions().transform(RoundedCorners(24)))
                    .error(R.drawable.acbso_ic_coin_error)
                    .into(vb.iconImage)
            }
            // endregion

            // region # text
            vb.title.text = if (displayTitle.isEmpty()) title else displayTitle
            vb.actionType.text = actionName
            vb.description.text = actionGuide
            vb.reward.text = reward?.ImpressionReward?.toLocale()
            vb.detailDescription.text = userGuide
            vb.confirmButton.text = if (productID == OfferwallProductType.CPI) {
                getString(R.string.acbso_offerwall_button_confirm_cpi)
            } else {
                getString(R.string.acbso_offerwall_button_confirm).format(reward?.ImpressionReward)
            }
            vb.validateButton.text = getString(R.string.acbso_offerwall_button_validate).format(reward?.ImpressionReward)
            // endregion

            // region # visible
            vb.iconBadge.isVisible = (journeyState == OfferwallJourneyStateType.PARTICIPATE)
            vb.validateButton.isVisible = (productID == OfferwallProductType.CPI)
            advertiseStatus = impressionItem.journeyState
            // endregion

            // region # onClick
            vb.confirmButton.setOnClickListener { requestConfirm(impressionItem = this) }
            vb.validateButton.setOnClickListener { requestValidate(impressionItem = this) }
            // endregion
        }
    }


    private fun isInstalled(packageName: String?): Boolean {
        this.packageManager.getLaunchIntentForPackage(packageName ?: "")?.run {
            return true
        }
        val installPackage = packageManager.getInstalledPackages(0)
        installPackage.forEach {
            if (it.packageName.equals(packageName)) {
                LogHandler.i(moduleName = this::class.java.simpleName) { "$tagName -> isInstalled -> $${it.packageName.equals(packageName)}" }
                return true
            }
        }
        return false
    }


    private fun requestConfirm(impressionItem: OfferwallImpressionItemEntity) {
        // TODO 연령심사

        CoreContractor.DeviceSetting.retrieveAAID { aaidEntity ->
            viewModel.requestConfirm(
                deviceADID = aaidEntity.aaid,
                advertiseID = parcel?.advertiseID ?: "",
                impressionID = impressionItem.impressionID,
                service = serviceType,
            )
        }
    }


    private fun requestConversion(impressionItem: OfferwallImpressionItemEntity) {
        CoreContractor.DeviceSetting.retrieveAAID { aaidEntity ->
            viewModel.requestConversion(
                deviceADID = aaidEntity.aaid,
                advertiseID = impressionItem.advertiseID,
                clickID = impressionItem.clickID ?: "",
                service = serviceType
            )
        }
    }


    private fun requestValidate(impressionItem: OfferwallImpressionItemEntity) {
        val isInstalled = isInstalled(impressionItem.packageName)
        if (isInstalled) {
            requestConversion(impressionItem = impressionItem)
        } else {
            MessageDialogHelper.determine(
                activity = this,
                message = R.string.acbso_offerwall_not_installed_click_after_installation,
                positiveText = R.string.acbso_offerwall_button_offerwall_install,
                onPositive = {
                    requestConfirm(impressionItem)
                },
                negativeText = R.string.acb_common_button_cancel,
            ).show(false)
        }
    }


    private fun requestClose() {
        CoreContractor.DeviceSetting.retrieveAAID { aaidEntity ->
            viewModel.requestClose(deviceADID = aaidEntity.aaid, advertiseID = parcel?.advertiseID ?: "")
        }
    }

    private fun actionCloseAdvertise() = requestClose()


    private fun actionRewardInquiry(entity: OfferwallImpressionItemEntity) {
        if (entity.contactState == 0 || entity.contactState == 1 || entity.contactState == 5 || entity.contactState == 8 || entity.contactState == 9) {
            val inquiryMessage = when (entity.contactState) {
                9 -> R.string.acbso_offerwall_inquiry_reward_contact_complete
                8 -> R.string.acbso_offerwall_inquiry_reward_contact_has_answer
                else -> R.string.acbso_offerwall_inquiry_reward_contact_ing
            }
            MessageDialogHelper.confirm(
                activity = this,
                message = inquiryMessage
            ).show(false)
            return
        }

        if (entity.clickDateTime == null) {
            return
        }

        // test
        InquiryRewardActivity.openForResult(
            activity = this@OfferwallDetailViewActivity,
            serviceType = serviceType,
            parcel = InquiryParcel(
                advertiseId = entity.advertiseID,
                contactId = null,
                title = entity.title,
                state = 0
            )
        )


//        val now = DateTime().millis
//        val participateTime: Long? = when (entity.productID) {
//            OfferwallProductType.CPFL,
//            OfferwallProductType.CPIF -> {
//                entity.clickDateTime?.plusHours(24)?.millis
//            }
//            else -> entity.clickDateTime?.plusMinutes(40)?.millis
//        }
//        if (now >= participateTime ?: 0) {
//            InquiryRewardActivity.openForResult(
//                activity = this@OfferwallDetailViewActivity,
//                serviceType = serviceType,
//                parcel = InquiryParcel(
//                    advertiseId = entity.advertiseID,
//                    contactId = null,
//                    title = entity.title,
//                    state = 0
//                )
//            )
//        } else {
//            MessageDialogHelper.confirm(
//                activity = this@OfferwallDetailViewActivity,
//                message = when (entity.productID) {
//                    OfferwallProductType.CPFL -> getString(R.string.acbso_offerwall_inquiry_reward_message_time_24_hour_not_yet)
//                    else -> getString(R.string.acbso_offerwall_inquiry_reward_message_time_40_minute_not_yet)
//                }
//            ).show(false)
//        }
    }


    private fun actionHideAdvertise(entity: OfferwallImpressionItemEntity) {
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
            vb.adClose.id -> {
                MessageDialogHelper.determine(
                    activity = this@OfferwallDetailViewActivity,
                    message = R.string.acbso_offerwall_do_you_want_to_remove_from_the_participating_list,
                    onPositive = {
                        actionCloseAdvertise()
                    }
                ).show(false)
            }
            vb.adRewardInquiry.id -> {
                actionRewardInquiry(impressionItemEntity)
            }
            vb.adHide.id -> {
                MessageDialogHelper.determine(
                    activity = this@OfferwallDetailViewActivity,
                    message = R.string.acbso_offerwall_do_you_want_to_hide_from_the_participating_list,
                    onPositive = {
                        actionHideAdvertise(impressionItemEntity)
                    }
                ).show(false)
            }
        }
    }
}