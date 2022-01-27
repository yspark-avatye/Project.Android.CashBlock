package com.avatye.cashblock.base.component.domain.entity.offerwall

data class OfferwallContactRewardsEntity(
    val adUniqueID: String = "",
    val contactID: String = "",
    val advertiseID: String = "",
    val title: String = "",
    val contents: String = "",
    val type: Int = 0,
    val state: Int = 0,
    val deviceID: String = "",
    val deviceADID: String = "",
    val phone: String = "",
    val userName: String = "",
    val resultMsgType: Int = 0,
    val customMsg: String = "",
)
