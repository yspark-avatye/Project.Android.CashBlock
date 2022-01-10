package com.avatye.cashblock.base.component.domain.entity.banner

data class BannerRewardEntity(
    val rewardable: Boolean = false,
    val reward: Int = 0,
    val transactionId: String = ""
)