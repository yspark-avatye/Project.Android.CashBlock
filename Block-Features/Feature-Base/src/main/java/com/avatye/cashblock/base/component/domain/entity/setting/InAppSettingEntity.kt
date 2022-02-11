package com.avatye.cashblock.base.component.domain.entity.setting

data class InAppSettingEntity(val main: Main) {

    data class Main(val rewardBannerDelay: Long = 1300L, val pid: PlacementID) {
        data class PlacementID(
            val rewardBanner: String,
            val linearSSP_320x50: String,
            val linearSSP_320x100: String,
            val linearNative_320x50: String,
            val linearNative_320x100: String
        )
    }

    companion object {
        fun empty(): InAppSettingEntity {
            val pid = Main.PlacementID(
                rewardBanner = "",
                linearSSP_320x50 = "",
                linearSSP_320x100 = "",
                linearNative_320x50 = "",
                linearNative_320x100 = ""
            )
            return InAppSettingEntity(main = Main(pid = pid))
        }
    }
}