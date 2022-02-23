package com.avatye.cashblock.feature.offerwall.component.controller

import com.avatye.cashblock.base.component.contract.business.AccountContractor
import com.avatye.cashblock.base.component.contract.business.SettingContractor
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallImpressionItemEntity
import com.avatye.cashblock.base.component.domain.entity.user.AgeVerifiedType
import com.avatye.cashblock.base.component.widget.banner.BannerLinearView
import com.avatye.cashblock.base.library.ad.curator.IADAgeVerifier
import com.avatye.cashblock.base.library.ad.curator.linear.loader.LinearADSize


internal object ADController {

    private val placementADSize = LinearADSize.W320XH100

    var impressionItemEntity = OfferwallImpressionItemEntity()

    private val verifier = object : IADAgeVerifier {
        override fun isVerified(): Boolean {
            return AccountContractor.ageVerified == AgeVerifiedType.VERIFIED
        }
    }

    fun createBannerData(): BannerLinearView.BannerData {
        val mezzo = BannerLinearView.MediationMezzoData(
            storeUrl = SettingContractor.appInfoSetting.storeUrl,
            allowBackground = false
        )
        return BannerLinearView.BannerData(
            placementAppKey = SettingContractor.advertiseNetworkSetting.igaWorks.appKey,
            placementADSize = placementADSize,
            sspPlacementID = makeSSPPID(linearADSize = placementADSize),
            nativePlacementID = makeNativePID(linearADSize = placementADSize),
            mezzo = mezzo,
            verifier = verifier
        )
    }

    private fun makeSSPPID(linearADSize: LinearADSize): String {
        return when (linearADSize) {
            LinearADSize.W320XH50 -> SettingContractor.inAppSetting.main.pid.linearSSP_320x50
            LinearADSize.W320XH100 -> SettingContractor.inAppSetting.main.pid.linearSSP_320x100
        }
    }

    private fun makeNativePID(linearADSize: LinearADSize): String {
        return when (linearADSize) {
            LinearADSize.W320XH50 -> SettingContractor.inAppSetting.main.pid.linearNative_320x50
            LinearADSize.W320XH100 -> SettingContractor.inAppSetting.main.pid.linearNative_320x100
        }
    }

}