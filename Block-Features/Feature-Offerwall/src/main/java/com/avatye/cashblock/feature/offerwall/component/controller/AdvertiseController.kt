package com.avatye.cashblock.feature.offerwall.component.controller

import com.avatye.cashblock.base.component.contract.AccountContract
import com.avatye.cashblock.base.component.contract.RemoteContract
import com.avatye.cashblock.base.component.domain.entity.user.AgeVerifiedType
import com.avatye.cashblock.base.component.widget.banner.BannerLinearView
import com.avatye.cashblock.base.library.ad.curator.IADAgeVerifier


internal object AdvertiseController {

    private val verifier = object : IADAgeVerifier {
        override fun isVerified(): Boolean {
            return AccountContract.ageVerified == AgeVerifiedType.VERIFIED
        }
    }

    fun createBannerData(): BannerLinearView.BannerData {
        val placementAppKey = RemoteContract.advertiseNetworkSetting.igaWorks.appKey
        val nativePlacementID = RemoteContract.inAppSetting.main.pid.linearNative
        val mezzo = BannerLinearView.MediationMezzoData(
            storeUrl = RemoteContract.appInfoSetting.storeUrl,
            allowBackground = false
        )
        return BannerLinearView.BannerData(
            placementAppKey = placementAppKey,
            sspPlacementID = RemoteContract.inAppSetting.main.pid.linearSSP,
            nativePlacementID = nativePlacementID,
            mezzo = mezzo,
            verifier = verifier
        )
    }
}