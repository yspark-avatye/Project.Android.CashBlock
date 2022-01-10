package com.avatye.cashblock.feature.roulette.presentation.view.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.CompoundButton
import androidx.core.view.isVisible
import com.avatye.cashblock.base.component.contract.EventBusContract
import com.avatye.cashblock.base.component.contract.RemoteContract
import com.avatye.cashblock.base.component.contract.ViewOpenContract
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.domain.entity.mission.MissionStateEntity
import com.avatye.cashblock.base.component.support.ChannelTalkUtil
import com.avatye.cashblock.base.component.support.MessageDialogHelper
import com.avatye.cashblock.base.component.support.launch
import com.avatye.cashblock.base.component.widget.header.HeaderView
import com.avatye.cashblock.feature.roulette.BuildConfig
import com.avatye.cashblock.feature.roulette.MODULE_NAME
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.component.controller.MissionController
import com.avatye.cashblock.feature.roulette.component.controller.NotificationController
import com.avatye.cashblock.feature.roulette.component.data.PreferenceData
import com.avatye.cashblock.feature.roulette.databinding.AcbsrActivitySettingMainBinding
import com.avatye.cashblock.feature.roulette.presentation.AppBaseActivity

internal class SettingMainActivity : AppBaseActivity() {

    companion object {
        fun open(activity: Activity, close: Boolean = false) {
            activity.launch(
                Intent(activity, SettingMainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                },
                transition = ActivityTransitionType.NONE.value,
                close = close
            )
        }
    }

    private var isShowAttendanceView = false
    private val vb: AcbsrActivitySettingMainBinding by lazy {
        AcbsrActivitySettingMainBinding.inflate(LayoutInflater.from(this))
    }

    override fun onResume() {
        super.onResume()
        vb.bannerLinearView.onResume()
    }

    override fun onPause() {
        super.onPause()
        vb.bannerLinearView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        vb.bannerLinearView.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(view = vb.root, logKey = "view:config")
        // header
        vb.headerView.actionClose { finish() }
        // notification
        vb.configNotificationContainer.isVisible = RemoteContract.appInfoSetting.allowTicketBox
        vb.configNotificationStatus.isChecked = NotificationController.SDK.allowNotification
        vb.configNotificationStatus.setOnCheckedChangeListener { buttonView, isChecked ->
            onCheckedChangeNotification(button = buttonView, isChecked = isChecked)
        }
        // notice
        vb.configNotice.setOnClickListener {
            ViewOpenContract.openNoticeList(
                activity = this@SettingMainActivity,
                blockCode = RouletteConfig.blockCode
            )
        }
        // cs
        vb.configCsInquire.setOnClickListener {
            ChannelTalkUtil.open(
                activity = this@SettingMainActivity,
                blockCode = RouletteConfig.blockCode,
                blockName = MODULE_NAME,
                fallback = {
                    MessageDialogHelper.requestSuggestion(
                        activity = this@SettingMainActivity,
                        blockCode = RouletteConfig.blockCode
                    )
                }
            )
        }
        // alliance
        vb.configAlliance.setOnClickListener {
            MessageDialogHelper.requestAlliance(
                activity = this@SettingMainActivity,
                blockCode = RouletteConfig.blockCode
            )
        }
        // terms
        vb.configTerms.setOnClickListener {
            ViewOpenContract.openTermsView(
                activity = this@SettingMainActivity,
                blockCode = RouletteConfig.blockCode,
                headerType = HeaderView.HeaderType.POPUP,
                close = false
            )
        }
        // version
        vb.configSdkVersion.text = "V${BuildConfig.X_BUILD_SDK_VERSION_NAME}"
        // attendance
        MissionController.Attendance.requestList(ownerActivity = this@SettingMainActivity) {
            it?.let { data ->
                vb.configEventContainer.isVisible = true
                vb.configMissionAttendance.setOnClickListener { showAttendanceView(data) }
            } ?: run {
                vb.configEventContainer.isVisible = false
            }
        }
    }

    private fun showAttendanceView(attendanceList: MutableList<MissionStateEntity>) {
        if (isShowAttendanceView) {
            MissionController.Attendance.requestList(ownerActivity = this@SettingMainActivity) {
                it?.let { data ->
                    MissionController.Attendance.showView(ownerActivity = this@SettingMainActivity, attendanceList = data)
                }
            }
        } else {
            isShowAttendanceView = true
            MissionController.Attendance.showView(ownerActivity = this@SettingMainActivity, attendanceList = attendanceList)
        }
    }

    private fun onCheckedChangeNotification(button: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            if (NotificationController.Host.useHostNotification) {
                PreferenceData.Notification.update(allow = isChecked)
                NotificationController.Host.setNotificationEnabled(context = this@SettingMainActivity)
                EventBusContract.postNotificationStatusUpdate()
            } else {
                NotificationController.SDK.activationNotification(activity = this@SettingMainActivity, callback = {
                    button?.isChecked = it
                })
            }
        } else {
            if (NotificationController.Host.useHostNotification) {
                PreferenceData.Notification.update(allow = isChecked)
                NotificationController.SDK.stopNotificationService(context = this@SettingMainActivity)
                EventBusContract.postNotificationStatusUpdate()
            } else {
                NotificationController.SDK.deactivationNotification(activity = this@SettingMainActivity)
                button?.isChecked = false
            }
        }
    }
}