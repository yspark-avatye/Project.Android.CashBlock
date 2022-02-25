package com.avatye.cashblock.base.component.domain.entity.offerwall

import org.joda.time.DateTime

data class OfferwallContactRewardEntity(
    val contactID: String = "",
    val advertiseID: String = "",
    val title: String = "",
    val state: Int = 0,
    val resultMsgType: Int = 0,
    val customMsg: String = "",
    val createDateTime: DateTime? = null
)
