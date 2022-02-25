package com.avatye.cashblock.feature.roulette.component.widget.banner.reward

internal interface IBannerLinearRewardCallback {
    fun onReward(rewardAmount: Int)
    fun onAdFail()
}