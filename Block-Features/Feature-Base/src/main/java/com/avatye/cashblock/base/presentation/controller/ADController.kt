package com.avatye.cashblock.base.presentation.controller

import com.avatye.cashblock.base.component.contract.business.AccountContractor
import com.avatye.cashblock.base.component.contract.business.SettingContractor
import com.avatye.cashblock.base.component.domain.entity.user.AgeVerifiedType
import com.avatye.cashblock.base.component.widget.banner.BannerLinearView
import com.avatye.cashblock.base.library.ad.curator.IADAgeVerifier
import com.avatye.cashblock.base.library.ad.curator.linear.loader.LinearADSize

internal object ADController {

    private val verifier = object : IADAgeVerifier {
        override fun isVerified(): Boolean {
            return AccountContractor.ageVerified == AgeVerifiedType.VERIFIED
        }
    }

    fun createBannerData(): BannerLinearView.BannerData {
        val placementAppKey = SettingContractor.advertiseNetworkSetting.igaWorks.appKey
        val mezzo = BannerLinearView.MediationMezzoData(
            storeUrl = SettingContractor.appInfoSetting.storeUrl,
            allowBackground = false
        )
        return BannerLinearView.BannerData(
            placementAppKey = placementAppKey,
            placementADSize = LinearADSize.W320XH100,
            sspPlacementID = SettingContractor.inAppSetting.main.pid.linearSSP_320x100,
            nativePlacementID = SettingContractor.inAppSetting.main.pid.linearNative_320x100,
            mezzo = mezzo,
            verifier = verifier
        )
    }
}