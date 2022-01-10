package com.avatye.cashblock.feature.roulette.component.model.entity.winner

import org.joda.time.DateTime

internal data class WinnerItemEntity(
    val message: String,
    val rewardText: String,
    val createDateTime: DateTime?
)