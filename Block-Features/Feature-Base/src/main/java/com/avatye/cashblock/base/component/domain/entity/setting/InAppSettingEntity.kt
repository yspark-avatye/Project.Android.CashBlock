package com.avatye.cashblock.base.component.domain.entity.setting

data class InAppSettingEntity(val main: Main) {

    data class Main(val rewardBannerDelay: Long = 1300L, val pid: PlacementID) {
        data class PlacementID(
            val rewardBanner: String,
            val linearSSP: String,
            val linearNative: String
        )
    }

    companion object {
        fun empty(): InAppSettingEntity {
            val pid = Main.PlacementID(
                rewardBanner = "804902",
                linearSSP = "scp7x60801w53n2",
                linearNative = "znasaubxycpwdip"
            )
            return InAppSettingEntity(main = Main(pid = pid))
        }
    }
}