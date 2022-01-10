package com.avatye.cashblock.feature.roulette.component.controller

import com.avatye.cashblock.base.component.contract.RemoteContract
import com.avatye.cashblock.base.component.support.DeviceUtil
import com.avatye.cashblock.base.component.support.MessageDialogHelper
import com.avatye.cashblock.feature.roulette.R
import com.avatye.cashblock.feature.roulette.component.data.PreferenceData
import com.avatye.cashblock.feature.roulette.component.widget.dialog.DialogPopupBatteryOptimizeView
import com.avatye.cashblock.feature.roulette.component.widget.dialog.DialogPopupNotificationView
import com.avatye.cashblock.feature.roulette.presentation.AppBaseActivity
import org.joda.time.DateTime

internal object DialogController {

    internal fun showBatteryOptimizePopup(activity: AppBaseActivity) {
        val currentDate = DateTime().toString("yyyyMMdd").toInt()
        val checkedDate = PreferenceData.Notification.batteryOptimizeDialogCheckDate
        if (currentDate in 1..checkedDate) {
            return
        }
        if (DeviceUtil.Battery.isOptimization(context = activity)) {
            return
        }
        val dialog = DialogPopupBatteryOptimizeView.create(activity)
        activity.putDialogView(dialog)
        dialog.show(cancelable = false)
    }

    internal fun showNotificationPopup(activity: AppBaseActivity) {
        val dialog = DialogPopupNotificationView.create(activity).apply {
            setActionCallback {
                if (NotificationController.SDK.notificationChannelEnabled(activity)) {
                    PreferenceData.Notification.update(allow = true, popupCheckTime = DateTime().millis)
                    NotificationController.SDK.startNotificationService(activity)
                } else {
                    showNotificationSettingDialog(activity)
                }
            }
            setCloseCallback {
                PreferenceData.Notification.update(popupCheckTime = DateTime().millis)
            }
        }
        activity.putDialogView(dialog)
        dialog.show(cancelable = false)
    }

    internal fun showNotificationSettingDialog(activity: AppBaseActivity) {
        val settingMessage = activity.getString(R.string.acbsr_string_notification_need_system_setting)
            .format(RemoteContract.appInfoSetting.appName)
        val messageDialog = MessageDialogHelper.determine(
            activity = activity,
            message = settingMessage,
            positiveText = R.string.acb_common_button_setting,
            negativeText = R.string.acb_common_button_cancel,
            onPositive = { MessageDialogHelper.showSystemSettingDialog(activity = activity) }
        )
        activity.putDialogView(messageDialog)
        messageDialog.show(cancelable = false)
    }

}