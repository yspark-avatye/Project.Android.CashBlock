package com.avatye.cashblock.base.component.domain.entity.game

data class GameEntity(
    val gameID: String = "",
    val title: String = "",
    val useTicketAmount: Int = 0,
    val iconUrl: String = "",
    val imageUrl: String = "",
    val titleTextColor: Int = 0
)