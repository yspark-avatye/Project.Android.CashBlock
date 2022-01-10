package com.avatye.cashblock.base.component.domain.entity.game

import org.joda.time.DateTime

data class WinnerMessageEntity(
    val participateID: String = "",
    val appID: String = "",
    val message: String = "",
    val rewardText: String = "",
    val winDateTime: DateTime? = null
)