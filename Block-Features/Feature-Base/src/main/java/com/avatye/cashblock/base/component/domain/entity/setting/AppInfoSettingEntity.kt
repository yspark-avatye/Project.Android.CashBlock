package com.avatye.cashblock.base.component.domain.entity.setting

data class AppInfoSettingEntity(
    val appName: String,
    val storeUrl: String,
    val pointName: String,
    val rouletteName: String,
    val rouletteCampaign: String,
    val tooltipMessage: String,
    val allowLinearAD: Boolean,
    val allowMoreMenu: Boolean,
    val allowTicketBox: Boolean,
    val allowAgeVerification: Boolean,
    val allowBlockOfferwall: Boolean
) {
    companion object {
        fun empty() = AppInfoSettingEntity(
            appName = "",
            storeUrl = "",
            pointName = "캐시",
            rouletteName = "룰렛",
            rouletteCampaign = "",
            tooltipMessage = "",
            allowLinearAD = false,
            allowMoreMenu = false,
            allowTicketBox = false,
            allowAgeVerification = false,
            allowBlockOfferwall = true
        )
    }

    fun equals(setting: AppInfoSettingEntity): Boolean {
        return (appName == setting.appName
                && storeUrl == setting.storeUrl
                && pointName == setting.pointName
                && rouletteName == setting.rouletteName
                && rouletteCampaign == setting.rouletteCampaign
                && tooltipMessage == setting.tooltipMessage
                && allowLinearAD == setting.allowLinearAD
                && allowMoreMenu == setting.allowMoreMenu
                && allowTicketBox == setting.allowTicketBox
                && allowAgeVerification == setting.allowAgeVerification
                && allowBlockOfferwall == setting.allowBlockOfferwall)
    }
}