package com.avatye.cashblock.base.component.domain.entity.setting

data class RewardBannerSettingEntity(
    val roulette: BannerNetwork,
    val offerwall: BannerNetwork
) {

    data class BannerNetwork(
        val manplus: BannerSetting,
        val quantumbit: BannerSetting
    )

    data class BannerSetting(
        val allowAd: Boolean,
        val rewardDelay: Long,
        val rewardFrequency: Long
    )

    companion object {
        fun empty() = RewardBannerSettingEntity(
            roulette = BannerNetwork(
                manplus = BannerSetting(
                    allowAd = true,
                    rewardDelay = 1300L,
                    rewardFrequency = 600000
                ),
                quantumbit = BannerSetting(
                    allowAd = true,
                    rewardDelay = 1300L,
                    rewardFrequency = 600000
                )
            ),
            offerwall = BannerNetwork(
                manplus = BannerSetting(
                    allowAd = true,
                    rewardDelay = 1300L,
                    rewardFrequency = 600000
                ),
                quantumbit = BannerSetting(
                    allowAd = true,
                    rewardDelay = 1300L,
                    rewardFrequency = 600000
                )
            )
        )
    }
}