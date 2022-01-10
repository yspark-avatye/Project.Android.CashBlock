package com.avatye.cashblock.feature.roulette.component.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.avatye.cashblock.base.component.contract.CoreContract
import com.avatye.cashblock.base.component.contract.EventBusContract
import com.avatye.cashblock.base.component.contract.RemoteContract
import com.avatye.cashblock.base.component.domain.entity.base.ActionType
import com.avatye.cashblock.base.component.domain.entity.base.LandingType
import com.avatye.cashblock.base.component.support.isScreenOn
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.RouletteConfig.logger
import com.avatye.cashblock.feature.roulette.R
import com.avatye.cashblock.feature.roulette.component.controller.NotificationController
import com.avatye.cashblock.feature.roulette.component.controller.TicketBoxController
import com.avatye.cashblock.feature.roulette.component.controller.TicketController
import com.avatye.cashblock.feature.roulette.component.data.PreferenceData
import com.avatye.cashblock.feature.roulette.component.livedata.TicketBalanceLiveData
import com.avatye.cashblock.feature.roulette.component.livedata.TouchTicketLiveData
import com.avatye.cashblock.feature.roulette.component.livedata.VideoTicketLiveData
import org.joda.time.DateTime

internal class SDKNotificationService : Service() {

    internal companion object {
        const val NAME: String = "SDKNotificationService"

        const val notificationId: Int = 999001

        val channelId: String
            get() {
                return "${RouletteConfig.blockCode.blockId}:${CoreContract.appPackageName}"
            }
    }

    private var isRegisteredReceiver: Boolean = false

    override fun onCreate() {
        logger.i { "$NAME -> onCreate" }
        super.onCreate()
    }

    override fun onDestroy() {
        logger.i { "$NAME -> onDestroy" }
        unregisterBroadcastReceiver()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        logger.i { "$NAME -> onBind" }
        return null
    }

    override fun onRebind(intent: Intent?) {
        logger.i { "$NAME -> onRebind" }
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        logger.i { "$NAME -> onUnbind" }
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logger.i { "$NAME -> onStartCommand" }
        intent?.let {
            registerBroadcastReceiver()
            registerNotification()
        }
        return START_STICKY
    }

    private fun registerNotification() {
        logger.i { "$NAME -> registerNotification" }
        startForeground(notificationId, makeNotificationBuilder().build())
    }

    private fun updateNotification() {
        if (PreferenceData.Notification.allow && !PreferenceData.Notification.allowHost) {
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .notify(notificationId, makeNotificationBuilder().build())
        }
    }

    private fun unregisterNotification() {
        logger.i { "$NAME -> unregisterNotification" }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true)
        } else {
            stopSelf()
        }
    }

    private fun registerBroadcastReceiver() {
        logger.i { "$NAME -> registerBroadcastReceiver" }
        if (!isRegisteredReceiver) {
            registerReceiver(actionReceiver, EventBusContract.makeWatcherFilter().apply {
                addAction(Intent.ACTION_TIME_TICK)
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(ActionType.APP_LAUNCH_MAIN.actionName)
            })
            isRegisteredReceiver = true
        }
    }

    private fun unregisterBroadcastReceiver() {
        logger.i { "$NAME -> unregisterBroadcastReceiver" }
        if (isRegisteredReceiver) {
            runCatching {
                unregisterReceiver(actionReceiver)
            }.onSuccess {
                isRegisteredReceiver = false
            }.onFailure {
                logger.e(throwable = it) { "$NAME -> unregisterReceivers" }
            }
        }
    }

    private fun makeNotificationBuilder(): NotificationCompat.Builder {
        val notifyBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)
            NotificationCompat.Builder(this, channelId)
        } else {
            NotificationCompat.Builder(this)
        }
        val remoteView = makeRemoteView()
        notifyBuilder.setOnlyAlertOnce(true)
        notifyBuilder.setOngoing(true)
        notifyBuilder.setSmallIcon(smallIconResource)
        notifyBuilder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
        notifyBuilder.setContent(remoteView)
        NotificationController.createNotificationPendingIntent(context = this@SDKNotificationService, landingType = LandingType.ROULETTE_MAIN)?.let {
            notifyBuilder.setContentIntent(it)
        }
        return notifyBuilder
    }

    private fun makeRemoteView(): RemoteViews {
        val notificationLayoutResId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            R.layout.acbsr_layout_notification
        } else {
            R.layout.acbsr_layout_notification_legacy
        }
        return RemoteViews(packageName, notificationLayoutResId).apply {
            // ICON
            this.setImageViewResource(R.id.notification_app_icon, largeIconResource)
            // APP-NAME
            this.setTextViewText(R.id.notification_app_name, notificationAppName)
            // ROULETTE-NAME
            this.setTextViewText(R.id.notification_roulette_name, notificationRouletteName)
            // TICKET-BALANCE
            this.setTextViewText(R.id.notification_ticket_balance, viewTicketBalanceText)
            // TICKET-CONDITION
            this.setTextViewText(R.id.notification_ticket_condition, viewTicketConditionText)
            this.setImageViewResource(R.id.notification_ticket_condition_frame, viewTicketConditionDrawable)
            // BOX-CONDITION
            this.setTextViewText(R.id.notification_box_condition, viewBoxConditionText)
            this.setImageViewResource(R.id.notification_box_condition_frame, viewBoxConditionDrawable)
            if (viewBoxAvailable) {
                NotificationController.createNotificationPendingIntent(
                    context = this@SDKNotificationService,
                    landingType = LandingType.ROULETTE_TICKET_BOX
                )?.let {
                    this.setOnClickPendingIntent(R.id.notification_box_condition_frame, it)
                } ?: run {
                    this.setOnClickPendingIntent(R.id.notification_box_condition_frame, null)
                }
            } else {
                this.setOnClickPendingIntent(R.id.notification_box_condition_frame, null)
            }
        }
    }

    private val notificationAppName: String
        get() {
            return if (RouletteConfig.notificationServiceConfig.title.isNotEmpty()) {
                RouletteConfig.notificationServiceConfig.title
            } else {
                RemoteContract.appInfoSetting.appName
            }
        }

    private val notificationRouletteName: String
        get() {
            return if (RouletteConfig.notificationServiceConfig.text.isNotEmpty()) {
                RouletteConfig.notificationServiceConfig.text
            } else {
                RemoteContract.appInfoSetting.rouletteName
            }
        }

    private val channelName: String
        get() {
            return if (RouletteConfig.notificationServiceConfig.channelName.isNotEmpty()) {
                RouletteConfig.notificationServiceConfig.channelName
            } else {
                "${RemoteContract.appInfoSetting.appName} 알림창 상태바".trim()
            }
        }

    private val smallIconResource: Int
        get() {
            return if (RouletteConfig.notificationServiceConfig.smallIconResourceId > 0) {
                RouletteConfig.notificationServiceConfig.smallIconResourceId
            } else {
                R.drawable.acbsr_ic_ticket_12x12
            }
        }

    private val largeIconResource: Int
        get() {
            return if (RouletteConfig.notificationServiceConfig.largeIconResourceId > 0) {
                RouletteConfig.notificationServiceConfig.largeIconResourceId
            } else {
                R.drawable.acbsr_ic_notification_large
            }
        }

    private val viewTicketBalanceText: String
        get() {
            return if (TicketBalanceLiveData.balance > 0) {
                "티켓 ${TicketBalanceLiveData.balance}장"
            } else {
                "티켓 0장"
            }
        }

    private val viewTicketConditionText: String
        get() {
            val condition = TouchTicketLiveData.receivableCount + VideoTicketLiveData.receivableCount
            return if (condition > 0) {
                "$condition"
            } else {
                "0"
            }
        }

    private val viewTicketConditionDrawable: Int
        get() {
            val condition = TouchTicketLiveData.receivableCount + VideoTicketLiveData.receivableCount
            return if (condition > 0) {
                R.drawable.acbsr_frame_notification_ticket_condition_on
            } else {
                R.drawable.acbsr_frame_notification_ticket_condition_off
            }
        }

    private val viewBoxAvailable: Boolean
        get() {
            return TicketBoxController.hasTicketBox
        }

    private val viewBoxConditionText: String
        get() {
            return when (viewBoxAvailable) {
                true -> "1"
                false -> "0"
            }
        }

    private val viewBoxConditionDrawable: Int
        get() {
            return when (viewBoxAvailable) {
                true -> R.drawable.acbsr_frame_notification_box_condition_on
                false -> R.drawable.acbsr_frame_notification_box_condition_off
            }
        }

    private val onExecuteHandler: Handler = Handler(Looper.getMainLooper())
    private val onExecuteRunnable: Runnable = Runnable {
        logger.i { "$NAME -> onExecuteRunnable" }
        updateNotification()
    }

    private fun requestUpdateHandler() {
        onExecuteHandler.removeCallbacks(onExecuteRunnable)
        onExecuteHandler.postDelayed(onExecuteRunnable, 1500)
        logger.i { "$NAME -> onExecuteHandler -> postDelayed" }
    }

    // region { check screen status }
    private var isScreenOn: Boolean = false
    private var isSyncable: Boolean = true
    private var syncTime: Long = 0L
    private val syncFrequency: Long = (5 * 60 * 1000) //5min
    private val actionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (val actionName = intent?.action) {
                Intent.ACTION_TIME_TICK -> {
                    if (isScreenOn) {
                        syncTicketCondition()
                    } else {
                        context?.let {
                            if (it.isScreenOn) {
                                isScreenOn = true
                                syncTicketCondition()
                            }
                        }
                    }
                    logger.i { "$NAME -> BroadcastReceiver { actionName: $actionName, screen: $isScreenOn, syncTicketCondition: $isScreenOn }" }
                }
                Intent.ACTION_SCREEN_ON -> {
                    isScreenOn = true
                    NotificationController.SDK.requestNotificationLog(eventParamValue = "avatye")
                    logger.i { "$NAME -> BroadcastReceiver { actionName: $actionName, screen: $isScreenOn }" }
                }
                Intent.ACTION_SCREEN_OFF -> {
                    isScreenOn = false
                    logger.i { "$NAME -> BroadcastReceiver { actionName: $actionName, screen: $isScreenOn }" }
                }
                ActionType.FORBIDDEN.actionName -> {
                    logger.i { "$NAME -> BroadcastReceiver { actionName: $actionName, action: unregisterNotification() }" }
                    unregisterNotification()
                }
                ActionType.APP_LAUNCH_MAIN.actionName -> {
                    logger.i { "$NAME -> BroadcastReceiver { actionName: $actionName, action: createNotificationPendingIntent() }" }
                    context?.let {
                        NotificationController.createNotificationPendingIntent(
                            context = context,
                            landingType = LandingType.ROULETTE_MAIN
                        )?.send()
                    }
                }
                ActionType.TICKET_BOX_UPDATE.actionName,
                ActionType.TICKET_BALANCE_UPDATE.actionName,
                ActionType.TOUCH_TICKET_CONDITION_UPDATE.actionName,
                ActionType.VIDEO_TICKET_CONDITION_UPDATE.actionName,
                ActionType.BOX_CONDITION_UPDATE.actionName -> {
                    logger.i { "$NAME -> BroadcastReceiver { actionName: $actionName, action: requestUpdateHandler() }" }
                    requestUpdateHandler()
                }
                else -> {
                    logger.i { "$NAME -> BroadcastReceiver { actionName: $actionName, action: nope }" }
                }
            }
        }
    }

    private fun syncTicketCondition() {
        logger.i { "$NAME -> syncTicketCondition { isSyncable: $isSyncable }" }
        if (isSyncable) {
            val frequencyTime = DateTime().millis - syncTime
            logger.i { "$NAME -> syncTicketCondition { frequencyTime: $frequencyTime, syncFrequency: $syncFrequency, syncTimeOver: ${frequencyTime >= syncFrequency} }" }
            if (frequencyTime >= syncFrequency) {
                isSyncable = false
                TicketController.Session.syncTicketCondition {
                    syncTime = DateTime().millis
                    isSyncable = true
                }
            }
        }
    }
    // endregion
}