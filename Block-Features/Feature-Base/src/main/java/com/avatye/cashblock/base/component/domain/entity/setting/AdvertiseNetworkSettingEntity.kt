package com.avatye.cashblock.base.component.domain.entity.setting

data class AdvertiseNetworkSettingEntity(val igaWorks: IGAWorks, val manPlus: ManPlus) {
    // iga-works
    data class IGAWorks(val appKey: String, val hashKey: String)

    // man-plus
    data class ManPlus(val publisherCode: Int, val mediaCode: Int)

    companion object {
        fun empty() = AdvertiseNetworkSettingEntity(
            igaWorks = IGAWorks(appKey = "111629390", hashKey = "55ee4177230247c0"),
            manPlus = ManPlus(publisherCode = 1349, mediaCode = 32203)
        )
    }
}