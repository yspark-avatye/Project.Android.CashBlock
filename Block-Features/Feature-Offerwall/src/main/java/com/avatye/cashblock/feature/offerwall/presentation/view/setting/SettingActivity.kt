package com.avatye.cashblock.feature.offerwall.presentation.view.setting

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.avatye.cashblock.base.component.contract.business.ViewOpenContractor
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.component.domain.model.parcel.ServiceNameParcel
import com.avatye.cashblock.base.component.support.ChannelTalkUtil
import com.avatye.cashblock.base.component.support.MessageDialogHelper
import com.avatye.cashblock.base.component.support.launch
import com.avatye.cashblock.base.component.support.setOnClickWithDebounce
import com.avatye.cashblock.base.component.widget.banner.BannerLinearView
import com.avatye.cashblock.base.component.widget.header.HeaderView
import com.avatye.cashblock.feature.offerwall.BuildConfig
import com.avatye.cashblock.feature.offerwall.OfferwallConfig
import com.avatye.cashblock.feature.offerwall.component.controller.ADController
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoActivitySettingBinding
import com.avatye.cashblock.feature.offerwall.presentation.AppBaseActivity
import com.avatye.cashblock.feature.offerwall.presentation.view.inquiry.InquiryListActivity

internal class SettingActivity : AppBaseActivity() {

    companion object {
        fun open(activity: Activity, serviceType: ServiceType, close: Boolean = false) {
            activity.launch(
                intent = Intent(activity, SettingActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra(ServiceNameParcel.NAME, ServiceNameParcel(serviceType = serviceType))
                },
                transition = ActivityTransitionType.NONE.value,
                close = close
            )
        }
    }

    private val vb: AcbsoActivitySettingBinding by lazy {
        AcbsoActivitySettingBinding.inflate(LayoutInflater.from(this))
    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(vb.root)
        // header
        with(vb.headerView) {
            actionBack { finish() }
            actionClose { finish() }
        }
        // notice
        vb.configNotice.setOnClickWithDebounce {
            ViewOpenContractor.openNoticeList(
                activity = this@SettingActivity,
                blockType = OfferwallConfig.blockType,
                headerType = HeaderView.HeaderType.SUB
            )
        }
        // support-inquiry-list
        vb.configRewardInquiry.setOnClickWithDebounce {
            InquiryListActivity.open(
                activity = this@SettingActivity,
                serviceType = serviceType,
                close = false
            )
        }
        // support-inquire
        vb.configCsInquire.setOnClickWithDebounce {
            ChannelTalkUtil.open(
                activity = this@SettingActivity,
                blockType = OfferwallConfig.blockType,
                fallback = {
                    MessageDialogHelper.requestSuggestion(
                        activity = this@SettingActivity,
                        blockType = OfferwallConfig.blockType
                    )
                }
            )
        }
        // alliance
        vb.configAlliance.setOnClickWithDebounce {
            MessageDialogHelper.requestAlliance(
                activity = this@SettingActivity,
                blockType = OfferwallConfig.blockType
            )
        }
        // terms
        vb.configTerms.setOnClickListener {
            ViewOpenContractor.openTermsView(
                activity = this@SettingActivity,
                blockType = OfferwallConfig.blockType,
                headerType = HeaderView.HeaderType.POPUP,
                close = false
            )
        }
        // version
        vb.configSdkVersion.text = "V${BuildConfig.X_BUILD_SDK_VERSION_NAME}"
        // banner
        vb.bannerLinearView.bannerData = ADController.createBannerData()
        vb.bannerLinearView.sourceType = BannerLinearView.SourceType.OFFERWALL
        vb.bannerLinearView.requestBanner()
    }
}