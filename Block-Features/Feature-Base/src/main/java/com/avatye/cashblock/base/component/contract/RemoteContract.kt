package com.avatye.cashblock.base.component.contract

import com.avatye.cashblock.base.internal.preference.RemotePreferenceData

object RemoteContract {
    // remote settings
    val appInfoSetting
        get() = RemotePreferenceData.appInfo

    val inAppSetting
        get() = RemotePreferenceData.inApp

    val advertiseNetworkSetting
        get() = RemotePreferenceData.advertiseNetwork

    val notificationSetting
        get() = RemotePreferenceData.notification

    val missionSetting
        get() = RemotePreferenceData.mission

    val touchTicketSetting
        get() = RemotePreferenceData.touchTicket

    val videoTicketSetting
        get() = RemotePreferenceData.videoTicket

    val ticketBoxSetting
        get() = RemotePreferenceData.ticketBox
}