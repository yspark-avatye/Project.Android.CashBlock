package com.avatye.cashblock.feature.offerwall.component.controller

import com.avatye.cashblock.base.component.contract.business.AccountContractor
import com.avatye.cashblock.base.component.contract.business.SettingContractor
import com.avatye.cashblock.base.component.domain.entity.user.AgeVerifiedType
import com.avatye.cashblock.base.component.widget.banner.BannerLinearView
import com.avatye.cashblock.base.library.ad.curator.IADAgeVerifier


internal object AdvertiseController {

    private val verifier = object : IADAgeVerifier {
        override fun isVerified(): Boolean {
            return AccountContractor.ageVerified == AgeVerifiedType.VERIFIED
        }
    }

    fun createBannerData(): BannerLinearView.BannerData {
        val placementAppKey = SettingContractor.advertiseNetworkSetting.igaWorks.appKey
        val nativePlacementID = SettingContractor.inAppSetting.main.pid.linearNative
        val mezzo = BannerLinearView.MediationMezzoData(
            storeUrl = SettingContractor.appInfoSetting.storeUrl,
            allowBackground = false
        )
        return BannerLinearView.BannerData(
            placementAppKey = placementAppKey,
            sspPlacementID = SettingContractor.inAppSetting.main.pid.linearSSP,
            nativePlacementID = nativePlacementID,
            mezzo = mezzo,
            verifier = verifier
        )
    }
}