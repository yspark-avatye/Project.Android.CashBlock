package com.avatye.cashblock.base.component.domain.entity.setting

data class TouchTicketSettingEntity(
    val period: Int,
    val limitCount: Int,
    val allowNoAd: Boolean,
    val popAD: PopAD,
    val pid: PlacementID
) {

    // popup ad setting
    data class PopAD(
        val position: Float,
        val interval: Int,
        val exclude: Boolean,
        val excludePosition: Float
    )

    // placement id setting
    data class PlacementID(
        val linearSSP_320x50: String,
        val linearSSP_320x100: String,
        val popupSSP: String,
        val popupNative: String,
        val openInterstitialSSP: String,
        val openInterstitialNative: String,
        val openInterstitialVideoSSP: String,
        val openBoxBannerSSP: String,
        val closeInterstitialSSP: String,
        val closeInterstitialNative: String
    )

    companion object {
        fun empty() = TouchTicketSettingEntity(
            period = 3600,
            limitCount = 2,
            allowNoAd = false,
            popAD = PopAD(
                interval = 7,
                position = 0.5F,
                exclude = false,
                excludePosition = 1.2F
            ),
            pid = PlacementID(
                linearSSP_320x50 = "",
                linearSSP_320x100 = "",
                popupSSP = "",
                popupNative = "",
                openInterstitialSSP = "",
                openInterstitialNative = "",
                openInterstitialVideoSSP = "",
                openBoxBannerSSP = "",
                closeInterstitialSSP = "",
                closeInterstitialNative = ""
            )
        )
    }
}