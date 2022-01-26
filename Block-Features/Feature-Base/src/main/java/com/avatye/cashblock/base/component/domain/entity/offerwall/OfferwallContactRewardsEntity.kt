package com.avatye.cashblock.base.component.domain.entity.offerwall

data class OfferwallContactRewardsEntity(
    var adUniqueID: String = "",
    var contactID: String = "",
    var advertiseID: String = "",
    var title: String = "",
    var contents: String = "",
    var type: Int = 0,
    var state: Int = 0,
    var deviceID: String = "",
    var deviceADID: String = "",
    var phone: String = "",
    var userName: String = "",
    var resultMsgType: Int = 0,
    var customMsg: String = "",
)
