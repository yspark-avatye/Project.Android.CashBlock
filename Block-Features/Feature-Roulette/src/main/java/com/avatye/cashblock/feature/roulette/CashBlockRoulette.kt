package com.avatye.cashblock.feature.roulette

import android.app.PendingIntent
import android.content.Context
import androidx.annotation.Keep
import com.avatye.cashblock.base.component.domain.entity.base.LandingType
import com.avatye.cashblock.feature.roulette.component.controller.EntryController
import com.avatye.cashblock.feature.roulette.component.controller.NotificationController
import com.avatye.cashblock.feature.roulette.component.controller.TicketBoxController
import com.avatye.cashblock.feature.roulette.component.controller.TicketController
import com.avatye.cashblock.feature.roulette.component.data.PreferenceData
import com.avatye.cashblock.feature.roulette.component.model.entity.notification.NotificationServiceConfig
import com.avatye.cashblock.feature.roulette.component.model.listener.ITicketBoxCount
import com.avatye.cashblock.feature.roulette.component.model.listener.ITicketCount
import com.avatye.cashblock.feature.roulette.component.model.listener.IUpdateNotification

@Keep
object CashBlockRoulette {

    internal fun connect(context: Context) = EntryController.connect(context = context)


    @JvmStatic
    fun open(context: Context) = EntryController.open(context = context)


    /**
     * 티켓 정보를 반환합니다.
     * balance: 소유 티켓 수
     * condition: 받을 수 있는 티켓 수 (터치티켓 + 동영상티켓)
     */
    @JvmStatic
    fun checkTicketCondition(listener: ITicketCount) {
        TicketController.Session.checkTicketCondition(listener = listener)
    }


    /**
     * 티켓박스 정보를 반환합니다.
     * condition: 받을 수 있는 티켓박스 수
     * condition <= 0 : Dimmed 이미지를 사용해 주세요.
     */
    @JvmStatic
    fun checkTicketBoxCondition(listener: ITicketBoxCount) {
        TicketBoxController.Session.checkTicketBoxCondition(listener = listener)
    }


    /**
     * 티켓박스 사용자의 알림창 상태바 상태값을 설정합니다.
     * NotificationServiceConfig.smallIconResourceId
     * NotificationServiceConfig.largeIconResourceId
     * NotificationServiceConfig.channelName
     * NotificationServiceConfig.title
     * NotificationServiceConfig.text
     */
    @JvmStatic
    fun setNotificationServiceConfig(notificationServiceConfig: NotificationServiceConfig) {
        RouletteConfig.notificationServiceConfig = notificationServiceConfig
    }


    object NotificationIntegration {
        /**
         * 룰렛@캐시블럭 통합 '알림창 상태바' 사용시 사용 됩니다.
         * CashBlockSDK.initialize() -> CashBlockRoulette.Notification.initialize()
         */
        @JvmStatic
        fun initialize(notificationServiceConfig: NotificationServiceConfig) {
            NotificationController.Host.useHostNotification = true
            RouletteConfig.notificationServiceConfig = notificationServiceConfig
        }

        /**
         * 캐시룰렛의 '알림창 상태바' 사용 여부를 반환 합니다.
         */
        @JvmStatic
        fun getSDKNotificationEnabled(): Boolean = PreferenceData.Notification.allow

        /**
         * 파트너사의 '알림창 상태바' 활성 상태를 설정합니다.
         * 파트너사읭 '알림창 상타바'의 활성 상태가 변경 된다면 해당 메서드를 통해 활성 여부를 전달해야 합니다.
         */
        @JvmStatic
        fun setHostNotificationEnabled(context: Context, enabled: Boolean) {
            PreferenceData.Notification.update(allowHost = enabled)
            NotificationController.Host.setNotificationEnabled(context = context)
        }

        /**
         * 파트너사의 '알림창 상태바'에 표시되는 소유티켓수량, 받을 수 있는 티켓, 박스의 수량, 캐시룰렛 '알림창 상태바' 상태값을 이벤트로 전달 합니다.
         * 이벤트를 통해 전달되는 값을 통해 '알림창 상태바'의 정보를 갱신 할 수 있습니다.
         * 사용중인 '알림창 상태바' 서비스에 등록 합니다.
         */
        @JvmStatic
        fun registerNotificationUpdateReceiver(context: Context, listener: IUpdateNotification) {
            NotificationController.Host.registerEventReceiver(context = context, listener = listener)
        }

        /**
         * 더이상 이벤트를 받지 않을 경우 사용 됩니다.
         */
        @JvmStatic
        fun unregisterNotificationUpdateReceiver(context: Context) {
            NotificationController.Host.unregisterEventReceiver(context = context)
        }

        /**
         * 티켓 정보를 반환합니다.
         * balance: 소유 티켓 수
         * condition: 받을 수 있는 티켓 수 (터치티켓 + 동영상티켓)
         * condition <= 0 : Dimmed 이미지를 사용해 주세요.
         */
        @JvmStatic
        fun getTicketCondition(listener: ITicketCount) = checkTicketCondition(listener = listener)

        /**
         * 티켓박스 정보를 반환합니다.
         * condition: 받을 수 있는 티켓박스 수
         * condition <= 0 : Dimmed 이미지를 사용해 주세요.
         * pendingIntent: 아이콘 클릭시 처리할 동작이 전달 됩니다. (티켓박스 수령 화면 이동)
         */
        @JvmStatic
        fun getTicketBoxCondition(listener: ITicketBoxCount) = checkTicketBoxCondition(listener = listener)


        @JvmStatic
        fun getTicketBoxPendingIntent(context: Context): PendingIntent? {
            return NotificationController.createNotificationPendingIntent(context, LandingType.ROULETTE_TICKET_BOX)
        }
    }
}