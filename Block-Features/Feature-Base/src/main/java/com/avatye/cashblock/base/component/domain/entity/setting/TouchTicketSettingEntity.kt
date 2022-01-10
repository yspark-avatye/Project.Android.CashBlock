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
        val linearSSP: String,
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
                linearSSP = "p3tn4qmn5jelkna",
                popupSSP = "kw0k3dxyr8f8qn7",
                popupNative = "qn8ljp30byj8sgo",
                openInterstitialSSP = "8thf53toueddyzn",
                openInterstitialNative = "2a2mg1yuz8qo04h",
                openInterstitialVideoSSP = "dsna6b20zl9o8vn",
                openBoxBannerSSP = "",
                closeInterstitialSSP = "2hg1rdpov1q5zwu",
                closeInterstitialNative = "efrdtmodtqdadbe"
            )
        )
    }
}