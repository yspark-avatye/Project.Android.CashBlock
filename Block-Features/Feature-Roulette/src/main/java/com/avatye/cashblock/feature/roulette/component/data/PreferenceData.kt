package com.avatye.cashblock.feature.roulette.component.data

import android.content.Context
import android.content.SharedPreferences
import android.graphics.PointF
import androidx.core.content.edit
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.MODULE_NAME
import org.joda.time.DateTime
import org.json.JSONObject

internal object PreferenceData {
    private const val tagName = "PreferenceData@Roulette"

    // region # ticket
    object Ticket {
        private var Balance = Preference.Ticket.balance
        val balance: Int get() = Balance

        private var TouchReceivableCount = Preference.Ticket.touchReceivableCount
        val touchReceivableCount: Int get() = TouchReceivableCount

        private var TouchReceivableSyncTime = Preference.Ticket.touchReceivableSyncTime
        val touchReceivableSyncTime: Long get() = TouchReceivableSyncTime

        private var VideoReceivableCount = Preference.Ticket.videoReceivableCount
        val videoReceivableCount: Int get() = VideoReceivableCount

        private var VideoReceivableSyncTime = Preference.Ticket.videoReceivableSyncTime
        val videoReceivableSyncTime: Long get() = VideoReceivableSyncTime

        private var AcquireTotalCount = Preference.Ticket.acquireTotalCount
        val acquireTotalCount: Int get() = AcquireTotalCount

        fun clear() {
            Preference.Ticket.clear()
            update(
                balance = Preference.Ticket.balance,
                touchReceivableCount = Preference.Ticket.touchReceivableCount,
                touchReceivableSyncTime = Preference.Ticket.touchReceivableSyncTime,
                videoReceivableCount = Preference.Ticket.videoReceivableCount,
                videoReceivableSyncTime = Preference.Ticket.videoReceivableSyncTime,
                acquireTotalCount = Preference.Ticket.acquireTotalCount
            )
        }

        fun update(
            balance: Int? = null,
            touchReceivableCount: Int? = null,
            touchReceivableSyncTime: Long? = null,
            videoReceivableCount: Int? = null,
            videoReceivableSyncTime: Long? = null,
            acquireTotalCount: Int? = null
        ) {
            balance?.let {
                Balance = it
                Preference.Ticket.balance = it
            }
            touchReceivableCount?.let {
                TouchReceivableCount = it
                Preference.Ticket.touchReceivableCount = it
            }
            touchReceivableSyncTime?.let {
                TouchReceivableSyncTime = it
                Preference.Ticket.touchReceivableSyncTime = it
            }
            videoReceivableCount?.let {
                VideoReceivableCount = it
                Preference.Ticket.videoReceivableCount = it
            }
            videoReceivableSyncTime?.let {
                VideoReceivableSyncTime = it
                Preference.Ticket.videoReceivableSyncTime = it
            }
            acquireTotalCount?.let {
                AcquireTotalCount = it
                Preference.Ticket.acquireTotalCount = it
            }
        }
    }
    // endregion


    // region # banner reward
    object BannerReward {
        private var Frequency = Preference.BannerReward.frequency
        val frequency: Long get() = Frequency

        private var Rewardable = Preference.BannerReward.rewardable
        val rewardable: Boolean get() = Rewardable

        private var Amount = Preference.BannerReward.amount
        val amount: Int get() = Amount

        private var TransactionId = Preference.BannerReward.transactionId
        val transactionId: String get() = TransactionId

        fun clear() {
            Preference.BannerReward.clear()
            update(
                frequency = Preference.BannerReward.frequency,
                rewardable = Preference.BannerReward.rewardable,
                amount = Preference.BannerReward.amount,
                transactionId = Preference.BannerReward.transactionId,
            )
        }

        fun update(frequency: Long? = null, rewardable: Boolean? = null, amount: Int? = null, transactionId: String? = null) {
            frequency?.let {
                Frequency = it
                Preference.BannerReward.frequency = it
            }
            rewardable?.let {
                Rewardable = it
                Preference.BannerReward.rewardable = it
            }
            amount?.let {
                Amount = it
                Preference.BannerReward.amount = it
            }
            transactionId?.let {
                TransactionId = it
                Preference.BannerReward.transactionId = it
            }
        }
    }
    // endregion


    // region # notification
    object Notification {
        private var Allow = Preference.Notification.allow
        val allow: Boolean get() = Allow

        private var AllowHost = Preference.Notification.allowHost
        val allowHost: Boolean get() = AllowHost

        private var PopupCheckTime = Preference.Notification.popupCheckTime
        val popupCheckTime: Long get() = PopupCheckTime

        private var BatteryOptimizeDialogCheckDate = Preference.Notification.batteryOptimizeDialogCheckDate
        val batteryOptimizeDialogCheckDate: Int get() = BatteryOptimizeDialogCheckDate

        private var SyncLogDate = Preference.Notification.syncLogDate
        val syncLogDate: String get() = SyncLogDate

        fun clear() {
            Preference.Notification.clear()
            update(
                allow = Preference.Notification.allow,
                popupCheckTime = Preference.Notification.popupCheckTime,
                batteryOptimizeDialogCheckDate = Preference.Notification.batteryOptimizeDialogCheckDate,
                syncLogDate = Preference.Notification.syncLogDate
            )
        }

        fun update(allow: Boolean? = null, allowHost: Boolean? = null, popupCheckTime: Long? = null, batteryOptimizeDialogCheckDate: Int? = null, syncLogDate: String? = null) {
            allow?.let {
                Allow = it
                Preference.Notification.allow = it
            }
            allowHost?.let {
                AllowHost = it
                Preference.Notification.allowHost = it
            }
            popupCheckTime?.let {
                PopupCheckTime = it
                Preference.Notification.popupCheckTime = it
            }
            batteryOptimizeDialogCheckDate?.let {
                BatteryOptimizeDialogCheckDate = it
                Preference.Notification.batteryOptimizeDialogCheckDate = it
            }
            syncLogDate?.let {
                SyncLogDate = it
                Preference.Notification.syncLogDate = it
            }
        }
    }
    // endregion


    // region # mission - attendance
    object Mission {
        object Attendance {
            private var CheckDate = Preference.Mission.checkDate
            private var ShowDate = Preference.Mission.showDate
            private var Complete = Preference.Mission.complete

            val needShowDialog: Boolean
                get() {
                    return ShowDate != DateTime().toString("yyyyMMdd")
                }

            val needRequestMission: Boolean
                get() {
                    return CheckDate != DateTime().toString("yyyyMMdd")
                            && !Complete
                }

            fun clear() {
                Preference.Mission.clear()
                update(
                    checkDateTime = Preference.Mission.checkDate,
                    showDateTime = Preference.Mission.showDate,
                    complete = Preference.Mission.complete
                )
            }

            fun update(checkDateTime: DateTime? = null, showDateTime: DateTime? = null, complete: Boolean? = null) {
                checkDateTime?.let {
                    CheckDate = it.toString("yyyyMMdd")
                    Preference.Mission.checkDate = it.toString("yyyyMMdd")
                }
                showDateTime?.let {
                    ShowDate = it.toString("yyyyMMdd")
                    Preference.Mission.showDate = it.toString("yyyyMMdd")
                }
                complete?.let {
                    Complete = it
                    Preference.Mission.complete = it
                }
            }

            private fun update(checkDateTime: String? = null, showDateTime: String? = null, complete: Boolean? = null) {
                checkDateTime?.let {
                    CheckDate = it
                    Preference.Mission.checkDate = it
                }
                showDateTime?.let {
                    ShowDate = it
                    Preference.Mission.showDate = it
                }
                complete?.let {
                    Complete = it
                    Preference.Mission.complete = it
                }
            }
        }

    }
    // endregion


    // region # ticket box
    object Box {
        private var TicketBoxCheckDate = Preference.Box.ticketBoxCheckDate
        val ticketBoxCheckDate: String get() = TicketBoxCheckDate

        fun clear() {
            Preference.Box.clear()
            update(ticketBoxCheckDate = Preference.Box.ticketBoxCheckDate)
        }

        fun update(ticketBoxCheckDate: String? = null) {
            ticketBoxCheckDate?.let {
                TicketBoxCheckDate = it
                Preference.Box.ticketBoxCheckDate = it
            }
        }
    }
    // endregion


    // region # popup-notice
    object PopupNotice {
        private var PopupCloseDate: LinkedHashMap<String, Int> = makePopupDataCollection(Preference.PopupNotice.popupCloseDate)
        val popupCloseDate: LinkedHashMap<String, Int> get() = PopupCloseDate

        fun clear() {
            Preference.PopupNotice.clear()
            update(popupCloseDate = makePopupDataCollection(Preference.PopupNotice.popupCloseDate))
        }

        fun update(popupCloseDate: Map<String, Int>? = null) {
            popupCloseDate?.let {
                PopupCloseDate = it as LinkedHashMap<String, Int>
                Preference.PopupNotice.popupCloseDate = JSONObject(it).toString()
            }
        }

        private fun makePopupDataCollection(collectionRawData: String): LinkedHashMap<String, Int> {
            val resultCollection = LinkedHashMap<String, Int>()
            if (collectionRawData.isNotEmpty()) {
                try {
                    val map = JSONObject(collectionRawData)
                    map.keys().asSequence().forEach {
                        resultCollection[it] = map.getInt(it)
                    }
                } catch (e: Exception) {
                    LogHandler.e(moduleName = MODULE_NAME, throwable = e) {
                        "$tagName -> makePopupCloseMap { rawString: [$collectionRawData] }"
                    }
                }
            }
            return resultCollection
        }
    }
    // endregion


    // region # first
    object First {
        private var IsFirstIntro = Preference.First.isFirstIntro
        val isFirstIntro: Boolean get() = IsFirstIntro

        private var IsFirstRouletteMessage = Preference.First.isFirstRouletteMessage
        val isFirstRouletteMessage: Boolean get() = IsFirstRouletteMessage

        private var IsFirstNotificationInducePopup = Preference.First.isFirstNotificationInducePopup
        val isFirstNotificationInducePopup: Boolean get() = IsFirstNotificationInducePopup

        fun clear() {
            Preference.First.clear()
            update(
                isFirstIntro = Preference.First.isFirstIntro,
                isFirstRouletteMessage = Preference.First.isFirstRouletteMessage,
                isFirstNotificationInducePopup = Preference.First.isFirstNotificationInducePopup
            )
        }

        fun update(
            isFirstIntro: Boolean? = null,
            isFirstRouletteMessage: Boolean? = null,
            isFirstNotificationInducePopup: Boolean? = null
        ) {
            isFirstIntro?.let {
                IsFirstIntro = it
                Preference.First.isFirstIntro = it
            }
            isFirstRouletteMessage?.let {
                IsFirstRouletteMessage = it
                Preference.First.isFirstRouletteMessage = it
            }
            isFirstNotificationInducePopup?.let {
                IsFirstNotificationInducePopup = it
                Preference.First.isFirstNotificationInducePopup = it
            }
        }
    }
    // endregion


    // region # floating button
    object FloatingLayout {
        private var ButtonPosition = PointF(
            Preference.FloatingLayout.buttonPositionX,
            Preference.FloatingLayout.buttonPositionY
        )
        val buttonPosition: PointF get() = ButtonPosition

        private var ShowTooltipMoveTip = Preference.FloatingLayout.showTooltipMoveTip
        val showTooltipMoveTip: Boolean get() = ShowTooltipMoveTip

        private var TooltipMessageCheckDate = Preference.FloatingLayout.tooltipMessageCheckDate
        val tooltipMessageCheckDate: Int get() = TooltipMessageCheckDate

        fun clear() {
            Preference.FloatingLayout.clear()
            update(
                buttonPosition = PointF(
                    Preference.FloatingLayout.buttonPositionX,
                    Preference.FloatingLayout.buttonPositionY
                ),
                showTooltipMoveTip = Preference.FloatingLayout.showTooltipMoveTip,
                tooltipMessageCheckDate = Preference.FloatingLayout.tooltipMessageCheckDate
            )
        }

        fun update(
            buttonPosition: PointF? = null,
            showTooltipMoveTip: Boolean? = null,
            tooltipMessageCheckDate: Int? = null
        ) {
            buttonPosition?.let {
                ButtonPosition = it
                Preference.FloatingLayout.buttonPositionX = it.x
                Preference.FloatingLayout.buttonPositionY = it.y
            }
            showTooltipMoveTip?.let {
                ShowTooltipMoveTip = it
                Preference.FloatingLayout.showTooltipMoveTip = it
            }
            tooltipMessageCheckDate?.let {
                TooltipMessageCheckDate = it
                Preference.FloatingLayout.tooltipMessageCheckDate = it
            }
        }
    }
    // endregion


    fun clearSession() {

    }

    // region # Preference
    private object Preference {
        private const val preferenceName = "block:roulette:local-setting"
        private val pref: SharedPreferences by lazy {
            RouletteConfig.application.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        }


        // region # ticket
        object Ticket {
            private const val BALANCE = "ticket:balance"
            private const val TOUCH_RECEIVABLE_COUNT = "ticket:touch:receivable-count"
            private const val TOUCH_RECEIVABLE_SYNC_TIME = "ticket:touch:receivable-sync-time"
            private const val VIDEO_RECEIVABLE_COUNT = "ticket:video:receivable-count"
            private const val VIDEO_RECEIVABLE_SYNC_TIME = "ticket:video:receivable-sync-time"
            private const val ACQUIRE_TOTAL_COUNT = "ticket:acquire-total-count"

            var balance: Int
                get() {
                    return pref.getInt(BALANCE, -1)
                }
                set(value) {
                    pref.edit { putInt(BALANCE, value) }
                }

            var touchReceivableCount: Int
                get() {
                    return pref.getInt(TOUCH_RECEIVABLE_COUNT, -1)
                }
                set(value) {
                    pref.edit { putInt(TOUCH_RECEIVABLE_COUNT, value) }
                }

            var touchReceivableSyncTime: Long
                get() {
                    return pref.getLong(TOUCH_RECEIVABLE_SYNC_TIME, 0)
                }
                set(value) {
                    pref.edit { putLong(TOUCH_RECEIVABLE_SYNC_TIME, value) }
                }

            var videoReceivableCount: Int
                get() {
                    return pref.getInt(VIDEO_RECEIVABLE_COUNT, -1)
                }
                set(value) {
                    pref.edit { putInt(VIDEO_RECEIVABLE_COUNT, value) }
                }

            var videoReceivableSyncTime: Long
                get() {
                    return pref.getLong(VIDEO_RECEIVABLE_SYNC_TIME, 0)
                }
                set(value) {
                    pref.edit { putLong(VIDEO_RECEIVABLE_SYNC_TIME, value) }
                }

            // abuse -> don't clear
            var acquireTotalCount: Int
                get() {
                    return pref.getInt(ACQUIRE_TOTAL_COUNT, 0)
                }
                set(value) {
                    pref.edit { putInt(ACQUIRE_TOTAL_COUNT, value) }
                }

            fun clear() {
                arrayOf(
                    BALANCE,
                    TOUCH_RECEIVABLE_COUNT,
                    TOUCH_RECEIVABLE_SYNC_TIME,
                    VIDEO_RECEIVABLE_COUNT,
                    VIDEO_RECEIVABLE_SYNC_TIME
                ).forEach { element ->
                    pref.edit { remove(element) }
                }
            }
        }
        // endregion


        // region # banner reward
        object BannerReward {
            private const val FREQUENCY = "banner-reward:cpc-frequency"
            var frequency: Long
                get() {
                    return pref.getLong(FREQUENCY, 0L)
                }
                set(value) {
                    pref.edit { putLong(FREQUENCY, value) }
                }

            private const val REWARDABLE = "banner-reward:cpc-rewardable"
            var rewardable: Boolean
                get() {
                    return pref.getBoolean(REWARDABLE, false)
                }
                set(value) {
                    pref.edit { putBoolean(REWARDABLE, value) }
                }

            private const val AMOUNT = "banner-reward:cpc-amount"
            var amount: Int
                get() {
                    return pref.getInt(AMOUNT, 12)
                }
                set(value) {
                    pref.edit { putInt(AMOUNT, value) }
                }

            private const val TRANSACTION_ID = "banner-reward:cpc-transaction-id"
            var transactionId: String
                get() {
                    return pref.getString(TRANSACTION_ID, "") ?: ""
                }
                set(value) {
                    pref.edit { putString(TRANSACTION_ID, value) }
                }

            fun clear() {
                arrayOf(
                    FREQUENCY,
                    REWARDABLE,
                    AMOUNT,
                    TRANSACTION_ID
                ).forEach { element ->
                    pref.edit { remove(element) }
                }
            }
        }
        // endregion


        // region # notification
        object Notification {
            private const val ALLOW = "notification:allow"
            var allow: Boolean
                get() {
                    return pref.getBoolean(ALLOW, false)
                }
                set(value) {
                    pref.edit { putBoolean(ALLOW, value) }
                }

            private const val ALLOW_HOST = "notification:host-allow"
            var allowHost: Boolean
                get() {
                    return pref.getBoolean(ALLOW_HOST, false)
                }
                set(value) {
                    pref.edit { putBoolean(ALLOW_HOST, value) }
                }

            private const val POPUP_CHECK_TIME = "notification:popup-check-time"
            var popupCheckTime: Long
                get() {
                    return pref.getLong(POPUP_CHECK_TIME, 0L)
                }
                set(value) {
                    pref.edit { putLong(POPUP_CHECK_TIME, value) }
                }

            private const val BATTERY_OPTIMIZE_DIALOG_CHECK_DATE = "notification:battery-optimize-check-date"
            var batteryOptimizeDialogCheckDate: Int
                get() {
                    return pref.getInt(BATTERY_OPTIMIZE_DIALOG_CHECK_DATE, 0)
                }
                set(value) {
                    pref.edit { putInt(BATTERY_OPTIMIZE_DIALOG_CHECK_DATE, value) }
                }

            private const val SYNC_LOG_DATE = "notification:sync-log-date"
            var syncLogDate: String
                get() {
                    return pref.getString(SYNC_LOG_DATE, "") ?: ""
                }
                set(value) {
                    pref.edit { putString(SYNC_LOG_DATE, value) }
                }

            fun clear() {
                arrayOf(
                    ALLOW,
                    POPUP_CHECK_TIME,
                    BATTERY_OPTIMIZE_DIALOG_CHECK_DATE,
                    SYNC_LOG_DATE
                ).forEach { element ->
                    pref.edit { remove(element) }
                }
            }
        }
        // endregion


        // region # mission
        object Mission {
            private const val MISSION_ATTENDANCE_CHECK_DATE = "mission:attendance:check-date"
            var checkDate: String
                get() {
                    return pref.getString(MISSION_ATTENDANCE_CHECK_DATE, "") ?: ""
                }
                set(value) {
                    pref.edit { putString(MISSION_ATTENDANCE_CHECK_DATE, value) }
                }

            private const val MISSION_ATTENDANCE_SHOW_DATE = "mission:attendance:show-date"
            var showDate: String
                get() {
                    return pref.getString(MISSION_ATTENDANCE_SHOW_DATE, "") ?: ""
                }
                set(value) {
                    pref.edit { putString(MISSION_ATTENDANCE_SHOW_DATE, value) }
                }

            private const val MISSION_ATTENDANCE_COMPLETE = "mission:attendance:complete"
            var complete: Boolean
                get() {
                    return pref.getBoolean(MISSION_ATTENDANCE_COMPLETE, false)
                }
                set(value) {
                    pref.edit { putBoolean(MISSION_ATTENDANCE_COMPLETE, value) }
                }

            fun clear() {
                arrayOf(
                    MISSION_ATTENDANCE_CHECK_DATE,
                    MISSION_ATTENDANCE_SHOW_DATE,
                    MISSION_ATTENDANCE_COMPLETE
                ).forEach { element ->
                    pref.edit { remove(element) }
                }
            }
        }
        // endregion


        // region # ticket box
        object Box {
            private const val TICKET_BOX_CHECK_DATE = "box:ticket:check-date"
            var ticketBoxCheckDate: String
                get() {
                    return pref.getString(TICKET_BOX_CHECK_DATE, "") ?: ""
                }
                set(value) {
                    pref.edit { putString(TICKET_BOX_CHECK_DATE, value) }
                }

            fun clear() {
                arrayOf(
                    TICKET_BOX_CHECK_DATE
                ).forEach { element ->
                    pref.edit { remove(element) }
                }
            }
        }
        // endregion


        // region # popup-notice
        object PopupNotice {
            private const val CLOSE_DATE = "popup-notice:close-date"
            var popupCloseDate: String
                get() {
                    return pref.getString(CLOSE_DATE, "") ?: ""
                }
                set(value) {
                    pref.edit { putString(CLOSE_DATE, value) }
                }

            fun clear() {
                arrayOf(
                    CLOSE_DATE
                ).forEach { element ->
                    pref.edit { remove(element) }
                }
            }
        }
        // endregion


        // region # floating button
        object FloatingLayout {
            private const val BUTTON_POSITION_X = "floating-layout:button-position-x"
            var buttonPositionX: Float
                get() {
                    return pref.getFloat(BUTTON_POSITION_X, 0F)
                }
                set(value) {
                    pref.edit { putFloat(BUTTON_POSITION_X, value) }
                }

            private const val BUTTON_POSITION_Y = "floating-layout:button-position-y"
            var buttonPositionY: Float
                get() {
                    return pref.getFloat(BUTTON_POSITION_Y, 0F)
                }
                set(value) {
                    pref.edit { putFloat(BUTTON_POSITION_Y, value) }
                }

            private const val SHOW_TOOLTIP_MOVE_TIP = "floating-layout:tooltip:show-move-tip"
            var showTooltipMoveTip: Boolean
                get() {
                    return pref.getBoolean(SHOW_TOOLTIP_MOVE_TIP, true)
                }
                set(value) {
                    pref.edit { putBoolean(SHOW_TOOLTIP_MOVE_TIP, value) }
                }

            private const val TOOLTIP_MESSAGE_CHECK_DATE = "floating-layout:tooltip:check-date"
            var tooltipMessageCheckDate: Int
                get() {
                    return pref.getInt(TOOLTIP_MESSAGE_CHECK_DATE, 0)
                }
                set(value) {
                    pref.edit { putInt(TOOLTIP_MESSAGE_CHECK_DATE, value) }
                }

            fun clear() {
                arrayOf(
                    BUTTON_POSITION_X,
                    BUTTON_POSITION_Y,
                    SHOW_TOOLTIP_MOVE_TIP,
                    TOOLTIP_MESSAGE_CHECK_DATE
                ).forEach { element ->
                    pref.edit { remove(element) }
                }
            }

        }
        // endregion


        // region # first
        object First {
            private const val INTRO = "first:intro"
            var isFirstIntro: Boolean
                get() {
                    return pref.getBoolean(INTRO, true)
                }
                set(value) {
                    pref.edit { putBoolean(INTRO, value) }
                }

            private const val NOTIFY_USE_AAID = "first:notify-use-aaid"
            var needNotifyUseAAID: Boolean
                get() {
                    return pref.getBoolean(NOTIFY_USE_AAID, true)
                }
                set(value) {
                    pref.edit { putBoolean(NOTIFY_USE_AAID, value) }
                }

            private const val ROULETTE_WINNER_MESSAGE = "first:roulette-winner-message"
            var isFirstRouletteMessage: Boolean
                get() {
                    return pref.getBoolean(ROULETTE_WINNER_MESSAGE, true)
                }
                set(value) {
                    pref.edit { putBoolean(ROULETTE_WINNER_MESSAGE, value) }
                }

            private const val NOTIFICATION_INDUCE_POPUP = "first:notification-induce-popup"
            var isFirstNotificationInducePopup: Boolean
                get() {
                    return pref.getBoolean(NOTIFICATION_INDUCE_POPUP, true)
                }
                set(value) {
                    pref.edit { putBoolean(NOTIFICATION_INDUCE_POPUP, value) }
                }

            fun clear() {
                arrayOf(
                    INTRO,
                    NOTIFY_USE_AAID,
                    ROULETTE_WINNER_MESSAGE,
                    NOTIFICATION_INDUCE_POPUP
                ).forEach { element ->
                    pref.edit { remove(element) }
                }
            }
        }
        // endregion

    }
    // endregion

}