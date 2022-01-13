package com.avatye.cashblock.feature.roulette.component.controller

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.api.CoreApiContractor
import com.avatye.cashblock.base.component.contract.business.AccountContractor
import com.avatye.cashblock.base.component.contract.business.EventContractor
import com.avatye.cashblock.base.component.contract.business.SettingContractor
import com.avatye.cashblock.base.component.domain.entity.base.ActionType
import com.avatye.cashblock.base.component.domain.entity.base.LandingType
import com.avatye.cashblock.base.component.domain.model.parcel.EventBusParcel
import com.avatye.cashblock.base.component.support.extraParcel
import com.avatye.cashblock.base.component.support.isScreenOn
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.RouletteConfig.logger
import com.avatye.cashblock.feature.roulette.component.data.PreferenceData
import com.avatye.cashblock.feature.roulette.component.model.listener.IUpdateNotification
import com.avatye.cashblock.feature.roulette.component.service.SDKNotificationService
import com.avatye.cashblock.feature.roulette.component.widget.dialog.DialogPopupNotificationView
import com.avatye.cashblock.feature.roulette.component.widget.dialog.DialogPopupTicketBoxView
import com.avatye.cashblock.feature.roulette.presentation.AppBaseActivity
import com.avatye.cashblock.feature.roulette.presentation.view.landing.LandingActivity
import org.joda.time.DateTime

internal object NotificationController {
    fun createNotificationPendingIntent(context: Context, landingType: LandingType): PendingIntent? {
        return when (landingType) {
            LandingType.ROULETTE_MAIN -> {
                val intent = context.packageManager.getLaunchIntentForPackage(context.packageName).apply {
                    this?.setPackage(null)
                    this?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                }
                PendingIntent.getActivity(context.applicationContext, 0, intent, 0)
            }
            LandingType.ROULETTE_TICKET_BOX,
            LandingType.ROULETTE_TOUCH_TICKET,
            LandingType.ROULETTE_VIDEO_TICKET -> {
                val intent = Intent(context, LandingActivity::class.java).apply {
                    action = landingType.value
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }
                PendingIntent.getActivity(context.applicationContext, 0, intent, 0)
            }
            else -> null
        }
    }

    object SDK {
        private const val NAME: String = "Notification#SDK"

        val allowNotification: Boolean
            get() = PreferenceData.Notification.allow

        // region { CashRoulette-Notification Running Service}
        internal fun isRunningNotificationService(context: Context): Boolean {
            val manager = context.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (service.service.className == SDKNotificationService::class.java.name) {
                    return notificationChannelEnabled(context)
                }
            }
            return false
        }
        // endregion


        // region { CashRoulette-Notification Channel enabled}
        internal fun notificationChannelEnabled(context: Context): Boolean {
            try {
                val areNotificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
                val channelImportance = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    logger.i { "AppNotificationManager -> notificationsEnabled -> channelImportance" }
                    val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    if (hasNotificationChannelID(notificationManager = notificationManager, channelId = SDKNotificationService.channelId)) {
                        val channel: NotificationChannel = notificationManager.getNotificationChannel(SDKNotificationService.channelId)
                        channel.importance != NotificationManager.IMPORTANCE_NONE
                    } else {
                        true
                    }
                } else {
                    true
                }
                logger.i { "AppNotificationManager { areNotificationsEnabled:${areNotificationsEnabled}, channelImportance:$channelImportance }" }
                return (areNotificationsEnabled && channelImportance)
            } catch (e: Exception) {
                logger.e(throwable = e) { "AppNotificationManager -> Exception -> return false" }
                return false
            }
        }


        private fun hasNotificationChannelID(notificationManager: NotificationManager, channelId: String): Boolean {
            var hasChannel: Boolean = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.let { manager ->
                    manager.notificationChannels?.forEach { channel ->
                        if (channel.id.equals(channelId)) {
                            hasChannel = true
                            return@forEach
                        }
                    }
                }
            }
            return hasChannel
        }
        // endregion


        // region { check Notification when system booting }
        fun checkAndStartNotification(context: Context, callback: (success: Boolean) -> Unit = {}) {
            if (!SettingContractor.appInfoSetting.allowTicketBox || !AccountContractor.isLogin) {
                callback(false)
                return
            }
            if (PreferenceData.Notification.allow) {
                startNotificationService(context)
                callback(true)
            } else {
                callback(false)
            }
        }
        // endregion


        // region { TicketBoxBanner }
        fun showTicketBoxBannerDialog(activity: AppBaseActivity) {
            val allow: Boolean = PreferenceData.Notification.allow
            val enabled: Boolean = notificationChannelEnabled(activity)

            // ACTIVATION - NOTIFICATION
            if (allow && enabled) {
                startNotificationService(activity)
            }
            val dialog = DialogPopupTicketBoxView.create(activity).apply {
                if (!(allow && enabled)) {
                    setActionCallback(callback = {
                        if (enabled) {
                            PreferenceData.Notification.update(allow = true)
                            startNotificationService(activity)
                        } else {
                            DialogController.showNotificationSettingDialog(activity)
                        }
                    })
                }
                setCloseCallback(callback = {})
            }
            activity.putDialogView(dialog)
            dialog.show(cancelable = false)
        }
        // endregion


        // region { induce-Notification }
        fun induceNotificationService(ownerActivity: AppBaseActivity) {
            // 1. notification allow
            val checkAllow = PreferenceData.Notification.allow
            if (checkAllow) {
                startNotificationService(ownerActivity)
                // BATTERY OPTIMIZE
                DialogController.showBatteryOptimizePopup(activity = ownerActivity)
                return
            }
            // 2. check popup first
            if (PreferenceData.First.isFirstNotificationInducePopup) {
                // first == true & acquire >= 5
                val checkTicketAcquireCount = PreferenceData.Ticket.acquireTotalCount >= SettingContractor.notificationSetting.induceTicketCount
                if (checkTicketAcquireCount) {
                    PreferenceData.First.update(isFirstNotificationInducePopup = false)
                    DialogController.showNotificationPopup(activity = ownerActivity)
                }
            } else {
                // first == false && check popup time
                val checkPopupTime = (DateTime().millis - PreferenceData.Notification.popupCheckTime) > (SettingContractor.notificationSetting.inducePeriod * 1000)
                if (checkPopupTime) {
                    DialogController.showNotificationPopup(activity = ownerActivity)
                }
            }
        }
        // endregion


        internal fun requestNotificationLog(eventParamValue: String) {
            val currentTIme = DateTime().toString("yyyyMMdd")
            if (currentTIme != PreferenceData.Notification.syncLogDate) {
                CoreApiContractor(blockType = RouletteConfig.blockType).let {
                    it.postEventLog(eventKey = "view:notification-bar", eventParam = hashMapOf("source" to eventParamValue))
                    PreferenceData.Notification.update(syncLogDate = currentTIme)
                }
            }
        }

        internal fun activationNotification(activity: AppBaseActivity, callback: (activation: Boolean) -> Unit = {}) {
            if (allowNotification) {
                callback(true)
                return
            } else {
                if (notificationChannelEnabled(activity)) {
                    startNotificationService(activity)
                    PreferenceData.Notification.update(allow = true)
                    callback(true)
                } else {
                    DialogController.showNotificationSettingDialog(activity)
                    callback(false)
                }
            }
        }

        internal fun deactivationNotification(activity: AppBaseActivity) {
            if (allowNotification) {
                stopNotificationService(activity)
                PreferenceData.Notification.update(allow = false)
            }
        }

        internal fun startNotificationService(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(context, SDKNotificationService::class.java))
            } else {
                context.startService(Intent(context, SDKNotificationService::class.java))
            }
        }

        internal fun stopNotificationService(context: Context) {
            context.stopService(Intent(context, SDKNotificationService::class.java))
        }
    }

    object Host {
        private const val NAME: String = "Notification#Host"
        private var isRegisteredReceiver: Boolean = false

        internal var useHostNotification: Boolean = false

        // region { Host Notification enabled }
        internal fun setNotificationEnabled(context: Context, callback: (isRunningNotification: Boolean) -> Unit = {}) {
            if (!SettingContractor.appInfoSetting.allowTicketBox || !useHostNotification) {
                return
            }
            when (PreferenceData.Notification.allowHost) {
                true -> {
                    SDK.stopNotificationService(context)
                    callback(true)
                }
                false -> {
                    if (SDK.allowNotification) {
                        SDK.startNotificationService(context)
                        callback(true)
                    } else {
                        SDK.stopNotificationService(context)
                        callback(false)
                    }
                }
            }
        }
        // endregion


        fun checkAndStartNotification(context: Context, callback: (success: Boolean) -> Unit = {}) {
            if (!SettingContractor.appInfoSetting.allowTicketBox || !AccountContractor.isLogin) {
                callback(false)
                return
            }
            setNotificationEnabled(context) {
                callback(it)
            }
        }


        fun showTicketBoxBannerDialog(activity: AppBaseActivity) {
            val allow: Boolean = PreferenceData.Notification.allow
            val allowHostApp: Boolean = PreferenceData.Notification.allowHost
            val enabled: Boolean = SDK.notificationChannelEnabled(activity)

            val dialog = DialogPopupTicketBoxView.create(activity).apply {
                if (!allow) {
                    setActionCallback(callback = {
                        if (allowHostApp) {
                            PreferenceData.Notification.update(allow = true)
                            setNotificationEnabled(context = activity) {
                                EventContractor.postNotificationStatusUpdate(blockType = BlockType.ROULETTE)
                            }
                        } else {
                            if (enabled) {
                                PreferenceData.Notification.update(allow = true)
                                SDK.startNotificationService(activity)
                            } else {
                                DialogController.showNotificationSettingDialog(activity)
                            }

                        }
                    })
                }
                setCloseCallback(callback = {})
            }
            activity.putDialogView(dialog)
            dialog.show(cancelable = false)
        }


        // region { induce-Notification }
        fun induceNotificationService(ownerActivity: AppBaseActivity) {
            // 1. notification allow
            val checkAllow = PreferenceData.Notification.allow
            if (checkAllow) {
                // BATTERY OPTIMIZE
                DialogController.showBatteryOptimizePopup(activity = ownerActivity)
                return
            }
            // 2. check popup first
            if (PreferenceData.First.isFirstNotificationInducePopup) {
                // first == true & acquire >= 5
                val checkTicketAcquireCount = PreferenceData.Ticket.acquireTotalCount >= SettingContractor.notificationSetting.induceTicketCount
                if (checkTicketAcquireCount) {
                    PreferenceData.First.update(isFirstNotificationInducePopup = false)
                    showNotificationPopup(activity = ownerActivity)
                }
            } else {
                // first == false && check popup time
                val checkPopupTime = (DateTime().millis - PreferenceData.Notification.popupCheckTime) > (SettingContractor.notificationSetting.inducePeriod * 1000)
                if (checkPopupTime) {
                    showNotificationPopup(activity = ownerActivity)
                }
            }
        }


        private fun showNotificationPopup(activity: AppBaseActivity) {
            val allowHostApp: Boolean = PreferenceData.Notification.allowHost
            val enabled: Boolean = SDK.notificationChannelEnabled(activity)
            val dialog = DialogPopupNotificationView.create(activity).apply {
                setActionCallback {
                    if (allowHostApp) {
                        PreferenceData.Notification.update(allow = true, popupCheckTime = DateTime().millis)
                        setNotificationEnabled(context = activity) {
                            EventContractor.postNotificationStatusUpdate(blockType = BlockType.ROULETTE)
                        }
                    } else {
                        if (enabled) {
                            PreferenceData.Notification.update(allow = true, popupCheckTime = DateTime().millis)
                            SDK.startNotificationService(activity)
                        } else {
                            DialogController.showNotificationSettingDialog(activity)
                        }
                    }
                }
                setCloseCallback {
                    PreferenceData.Notification.update(popupCheckTime = DateTime().millis)
                }
            }
            activity.putDialogView(dialog)
            dialog.show(cancelable = false)
        }


        // region { Update Receiver (Host-App) }
        fun registerEventReceiver(context: Context, listener: IUpdateNotification) {
            logger.i { "$NAME -> registerEventReceiver" }
            val allowHostApp = PreferenceData.Notification.allowHost
            if (!useHostNotification || !allowHostApp) {
                return
            }
            HostAppActionReceiver.updateListener = listener
            if (!isRegisteredReceiver) {
                context.registerReceiver(HostAppActionReceiver, EventContractor.makeWatcherFilter().apply {
                    addAction(Intent.ACTION_TIME_TICK)
                    addAction(Intent.ACTION_SCREEN_ON)
                    addAction(Intent.ACTION_SCREEN_OFF)
                })
                isRegisteredReceiver = true
            }
        }

        fun unregisterEventReceiver(context: Context) {
            logger.i { "$NAME -> unregisterEventReceiver" }
            if (isRegisteredReceiver) {
                runCatching {
                    context.unregisterReceiver(HostAppActionReceiver)
                }.onSuccess {
                    isRegisteredReceiver = false
                }.onFailure {
                    logger.e(throwable = it) { "$NAME -> unregisterReceivers" }
                }
            }
        }

        object HostAppActionReceiver : BroadcastReceiver() {
            private var isAppScreenOn: Boolean = false
            private var isAppSyncable: Boolean = true
            private var appSyncTime: Long = 0L
            private const val appSyncFrequency: Long = (5 * 60 * 1000) //5min
            lateinit var updateListener: IUpdateNotification

            override fun onReceive(context: Context?, intent: Intent?) {
                val parel: EventBusParcel? = intent?.extraParcel(EventBusParcel.NAME)
                when (val actionName = intent?.action) {
                    Intent.ACTION_TIME_TICK -> {
                        if (PreferenceData.Notification.allow) {
                            if (isAppScreenOn) {
                                syncAppTicketCondition()
                            } else {
                                context?.let {
                                    if (it.isScreenOn) {
                                        isAppScreenOn = true
                                        syncAppTicketCondition()
                                    }
                                }
                            }
                            logger.i { "$NAME -> BroadcastReceiver(Host-App) { actionName: $actionName, screen: $isAppScreenOn, syncAppTicketCondition: $isAppScreenOn }" }
                        }
                    }
                    Intent.ACTION_SCREEN_ON -> {
                        if (PreferenceData.Notification.allow) {
                            isAppScreenOn = true
                            SDK.requestNotificationLog(eventParamValue = "nexonplay")
                            logger.i { "$NAME -> BroadcastReceiver(Host-App) { actionName: $actionName, screen: $isAppScreenOn }" }
                        }
                    }
                    Intent.ACTION_SCREEN_OFF -> {
                        if (PreferenceData.Notification.allow) {
                            isAppScreenOn = false
                            logger.i { "$NAME -> BroadcastReceiver(Host-App) { actionName: $actionName, screen: $isAppScreenOn }" }
                        }
                    }

                    ActionType.NOTIFICATION_STATUS_UPDATE.actionName -> {
                        logger.i { "$NAME -> BroadcastReceiver(Host-App) { actionName: $actionName, screen: $isAppScreenOn }" }
                        updateListener.onStatusChanged(isActive = PreferenceData.Notification.allow)
                    }
                    else -> {
                        if (PreferenceData.Notification.allow) {
                            updateListener.onTicketChanged()
                            logger.i { "$NAME -> BroadcastReceiver(Host-App) { actionName: $actionName, action: nope }" }
                        }
                    }
                }
            }

            private fun syncAppTicketCondition() {
                logger.i { "$NAME -> syncAppTicketCondition { isAppSyncable: $isAppSyncable }" }
                if (isAppSyncable) {
                    val appFrequencyTime = DateTime().millis - appSyncTime
                    logger.i { "$NAME -> syncAppTicketCondition { appFrequencyTime: $appFrequencyTime, appSyncFrequency: $appSyncFrequency, syncTimeOver: ${appFrequencyTime >= appSyncFrequency} }" }
                    if (appFrequencyTime >= appSyncFrequency) {
                        isAppSyncable = false
                        TicketController.Session.syncTicketCondition {
                            appSyncTime = DateTime().millis
                            isAppSyncable = true
                        }
                    }
                }
            }
        }
        // endregion
    }
}