package com.avatye.cashblock.feature.offerwall.presentation.view.inquiry

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.avatye.cashblock.base.component.contract.business.CoreContractor
import com.avatye.cashblock.base.component.contract.business.ViewOpenContractor
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult
import com.avatye.cashblock.base.component.support.*
import com.avatye.cashblock.base.component.widget.header.HeaderView
import com.avatye.cashblock.feature.offerwall.OfferwallConfig
import com.avatye.cashblock.feature.offerwall.R
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoActivityInquiryRewardBinding
import com.avatye.cashblock.feature.offerwall.presentation.AppBaseActivity
import com.avatye.cashblock.feature.offerwall.presentation.parcel.InquiryParcel
import com.avatye.cashblock.feature.offerwall.presentation.viewmodel.inquiry.InquiryViewModel

internal class InquiryRewardActivity : AppBaseActivity() {
    companion object {
        fun open(activity: Activity, parcel: InquiryParcel, close: Boolean = false) {
            activity.launch(
                intent = Intent(activity, InquiryRewardActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra(InquiryParcel.NAME, parcel)
                },
                transition = ActivityTransitionType.NONE.value,
                close = close
            )
        }
    }

    private lateinit var parcel: InquiryParcel
    private val viewModel = InquiryViewModel()
    private val vb: AcbsoActivityInquiryRewardBinding by lazy {
        AcbsoActivityInquiryRewardBinding.inflate(LayoutInflater.from(this))
    }

    private val phoneNumber: String
        get() {
            return vb.inquiryPhoneFirst.text.toString() + vb.inquiryPhoneMiddle.text.toString() + vb.inquiryPhoneLast.text.toString()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        extraParcel<InquiryParcel>(InquiryParcel.NAME)?.let {
            parcel = it
        } ?: run {
            MessageDialogHelper.confirm(
                activity = this@InquiryRewardActivity,
                message = R.string.acb_common_message_error,
                onConfirm = { finish() }
            ).show(cancelable = false)
        }
        setContentViewWith(vb.root)
        // header
        with(vb.headerView) {
            actionBack { finish() }
            updateTitleText(parcel.title)
        }
        // term
        vb.termAgree.setOnClickWithDebounce {
            ViewOpenContractor.openTermsView(
                activity = this@InquiryRewardActivity,
                blockType = OfferwallConfig.blockType,
                headerType = HeaderView.HeaderType.POPUP,
                url = "https://www.avatye.com/policy/privacy?type=embeded",
                close = false
            )
        }
        // load > inquiry info
        viewModel.requestInquiryInfo(advertiseID = parcel.advertiseId) {
            when (it) {
                is ViewModelResult.InProgress -> {
                    loadingView?.show(cancelable = false)
                }
                is ViewModelResult.Error -> {
                    loadingView?.dismiss()
                }
                is ViewModelResult.Complete -> {
                    loadingView?.dismiss()
                    vb.inquiryContent.setText(it.result)
                }
            }
        }
        // confirm
        vb.inquiryButton.setOnClickWithDebounce {
            if (verifyInquiry()) {
                confirm()
            }
        }
    }

    private fun confirm() {
        CoreContractor.DeviceSetting.retrieveAAID { device ->
            // 메세지 변경 필요
            if (!device.isValid) {
                CoreContractor.DeviceSetting.showLimitAdTrackingDialog(activity = this@InquiryRewardActivity)
            } else {
                viewModel.postInquiry(
                    contactID = parcel.contactId,
                    advertiseID = parcel.advertiseId,
                    title = parcel.title,
                    contents = vb.inquiryContent.text.toString(),
                    state = parcel.state,
                    deviceADID = device.aaid,
                    phone = phoneNumber,
                    userName = vb.inquiryName.text.toString()
                ) {
                    when (it) {
                        is ViewModelResult.InProgress -> loadingView?.show(cancelable = false)
                        is ViewModelResult.Error -> {
                            loadingView?.dismiss()
                            MessageDialogHelper.confirm(
                                activity = this@InquiryRewardActivity,
                                message = it.message
                            ).show(cancelable = false)
                        }
                        is ViewModelResult.Complete -> {
                            loadingView?.dismiss()
                            setResult(Activity.RESULT_OK)
                            MessageDialogHelper.confirm(
                                activity = this@InquiryRewardActivity,
                                messageSequence = getString(R.string.acbso_inquiry_message_complete).toHtml,
                                onConfirm = { finish() }
                            ).show(cancelable = false)
                        }
                    }
                }
            }
        }
    }

    private fun verifyInquiry(): Boolean {
        return when {
            !(vb.inquiryName.text?.toString()?.verifyName ?: false) -> {
                CoreUtil.showToast(message = R.string.acb_common_message_is_not_available_name)
                false
            }
            !phoneNumber.verifyPhoneNumber -> {
                CoreUtil.showToast(message = R.string.acb_common_message_is_not_available_phone_num)
                false
            }
            !vb.agreeServiceCheckbox.isChecked -> {
                CoreUtil.showToast(message = R.string.acb_common_message_is_not_agree_service)
                false
            }
            else -> true
        }
    }
}