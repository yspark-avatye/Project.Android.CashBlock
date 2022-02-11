package com.avatye.cashblock.base.component.domain.entity.setting

data class VideoTicketSettingEntity(
    val period: Int,
    val limitCount: Int,
    val pid: PlacementID
) {
    // placement id setting
    data class PlacementID(
        val linearSSP_320x50: String,
        val linearSSP_320x100: String,
        val openRewardVideoSSP: String,
        val openInterstitialSSP: String,
        val openInterstitialNative: String,
        val openBoxBannerSSP: String,
        val closeInterstitialSSP: String,
        val closeInterstitialNative: String
    )

    companion object {
        fun empty() = VideoTicketSettingEntity(
            period = 21600,
            limitCount = 2,
            pid = PlacementID(
                linearSSP_320x50 = "",
                linearSSP_320x100 = "",
                openRewardVideoSSP = "",
                openInterstitialSSP = "",
                openInterstitialNative = "",
                openBoxBannerSSP = "",
                closeInterstitialSSP = "",
                closeInterstitialNative = ""
            )
        )
    }
}