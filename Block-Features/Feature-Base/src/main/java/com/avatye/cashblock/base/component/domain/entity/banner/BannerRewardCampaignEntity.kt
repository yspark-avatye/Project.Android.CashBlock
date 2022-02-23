package com.avatye.cashblock.base.component.domain.entity.banner

data class BannerRewardCampaignEntity(
    val advertiseID: String = "",
    val imageUrl: String = "",
    val reward: Int = 0,
    val limitSeconds: Long = 0L
)