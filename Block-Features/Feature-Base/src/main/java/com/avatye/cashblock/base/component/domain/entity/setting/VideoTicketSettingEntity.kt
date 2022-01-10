package com.avatye.cashblock.base.component.domain.entity.setting

data class VideoTicketSettingEntity(
    val period: Int,
    val limitCount: Int,
    val pid: PlacementID
) {
    // placement id setting
    data class PlacementID(
        val linearSSP: String,
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
                linearSSP = "ybto6rbmi83wual",
                openRewardVideoSSP = "pzwkcn6n42xracd",
                openInterstitialSSP = "qpz6unvdbj0i034",
                openInterstitialNative = "3c588aycu7l8rzx",
                openBoxBannerSSP = "",
                closeInterstitialSSP = "ffu98vvbec2me9i",
                closeInterstitialNative = "oeoovzyl6m20x9e"
            )
        )
    }
}