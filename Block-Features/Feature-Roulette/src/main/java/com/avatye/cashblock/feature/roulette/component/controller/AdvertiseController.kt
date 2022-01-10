package com.avatye.cashblock.feature.roulette.component.controller

import android.app.Activity
import android.content.Context
import com.avatye.cashblock.base.component.contract.AccountContract
import com.avatye.cashblock.base.component.contract.RemoteContract
import com.avatye.cashblock.base.component.domain.entity.user.AgeVerifiedType
import com.avatye.cashblock.base.component.widget.banner.BannerLinearView
import com.avatye.cashblock.base.library.ad.curator.ADNetworkType
import com.avatye.cashblock.base.library.ad.curator.IADAgeVerifier
import com.avatye.cashblock.base.library.ad.curator.popup.CuratorPopup
import com.avatye.cashblock.base.library.ad.curator.popup.ICuratorPopupCallback
import com.avatye.cashblock.base.library.ad.curator.queue.CuratorQueue
import com.avatye.cashblock.base.library.ad.curator.queue.CuratorQueueEntity
import com.avatye.cashblock.base.library.ad.curator.queue.ICuratorQueueCallback
import com.avatye.cashblock.base.library.ad.curator.queue.loader.ADLoaderType
import com.avatye.cashblock.feature.roulette.component.model.entity.ADPlacementType
import com.avatye.cashblock.feature.roulette.component.model.entity.ADQueueType
import com.avatye.cashblock.feature.roulette.component.model.entity.BannerLinearPlacementType


internal object AdvertiseController {

    fun allowExcludeADNetwork(placementType: ADPlacementType, networkNo: Int): Boolean {
        if (networkNo == ADNetworkType.ADFIT.value) {
            //fixed
            return true
        }

        if (networkNo == ADNetworkType.MOBON.value) {
            // check setting
            return when (placementType) {
                ADPlacementType.TOUCH_TICKET -> RemoteContract.touchTicketSetting.popAD.exclude
                ADPlacementType.TICKET_BOX -> RemoteContract.ticketBoxSetting.popAD.exclude
            }
        }

        // other
        return false
    }

    private val verifier = object : IADAgeVerifier {
        override fun isVerified(): Boolean {
            return AccountContract.ageVerified == AgeVerifiedType.VERIFIED
        }
    }

    fun createBannerData(placementType: BannerLinearPlacementType): BannerLinearView.BannerData {
        val placementAppKey = RemoteContract.advertiseNetworkSetting.igaWorks.appKey
        val nativePlacementID = RemoteContract.inAppSetting.main.pid.linearNative
        val mezzo = BannerLinearView.MediationMezzoData(
            storeUrl = RemoteContract.appInfoSetting.storeUrl,
            allowBackground = false
        )

        return when (placementType) {
            BannerLinearPlacementType.COMMON -> {
                BannerLinearView.BannerData(
                    placementAppKey = placementAppKey,
                    sspPlacementID = RemoteContract.inAppSetting.main.pid.linearSSP,
                    nativePlacementID = nativePlacementID,
                    mezzo = mezzo,
                    verifier = verifier
                )
            }
            BannerLinearPlacementType.TOUCH_TICKET -> {
                BannerLinearView.BannerData(
                    placementAppKey = placementAppKey,
                    sspPlacementID = RemoteContract.touchTicketSetting.pid.linearSSP,
                    nativePlacementID = nativePlacementID,
                    mezzo = mezzo,
                    verifier = verifier
                )
            }
            BannerLinearPlacementType.VIDEO_TICKET -> {
                BannerLinearView.BannerData(
                    placementAppKey = placementAppKey,
                    sspPlacementID = RemoteContract.videoTicketSetting.pid.linearSSP,
                    nativePlacementID = nativePlacementID,
                    mezzo = mezzo,
                    verifier = verifier
                )
            }
            BannerLinearPlacementType.TICKET_BOX -> {
                BannerLinearView.BannerData(
                    placementAppKey = placementAppKey,
                    sspPlacementID = RemoteContract.ticketBoxSetting.pid.linearSSP,
                    nativePlacementID = nativePlacementID,
                    mezzo = mezzo,
                    verifier = verifier
                )
            }
        }
    }

    fun createADCuratorPopup(context: Context, placementType: ADPlacementType, callback: ICuratorPopupCallback): CuratorPopup {
        val placementAppKey = RemoteContract.advertiseNetworkSetting.igaWorks.appKey
        val sspPlacementID = when (placementType) {
            ADPlacementType.TOUCH_TICKET -> RemoteContract.touchTicketSetting.pid.popupSSP
            ADPlacementType.TICKET_BOX -> RemoteContract.ticketBoxSetting.pid.popupSSP
        }
        val nativePlacementID = when (placementType) {
            ADPlacementType.TOUCH_TICKET -> RemoteContract.touchTicketSetting.pid.popupNative
            ADPlacementType.TICKET_BOX -> RemoteContract.ticketBoxSetting.pid.popupNative
        }
        return CuratorPopup(
            context = context,
            placementAppKey = placementAppKey,
            sspPlacementID = sspPlacementID,
            nativePlacementID = nativePlacementID,
            mediationExtraData = null,
            verifier = verifier,
            callback = callback
        )
    }


    fun createADCuratorQueue(activity: Activity, adQueueType: ADQueueType, callback: ICuratorQueueCallback): CuratorQueue {
        val placementAppKey = RemoteContract.advertiseNetworkSetting.igaWorks.appKey
        val sequential: List<CuratorQueueEntity> = when (adQueueType) {
            // touch ticket
            ADQueueType.TOUCH_TICKET_OPEN -> makeQueueTouchTicketOpen()
            ADQueueType.TOUCH_TICKET_CLOSE -> makeQueueTouchTicketClose()
            // video ticket
            ADQueueType.VIDEO_TICKET_OPEN -> makeQueueVideoTicketOpen()
            ADQueueType.VIDEO_TICKET_CLOSE -> makeQueueVideoTicketClose()
            // ticket box
            ADQueueType.TICKET_BOX_OPEN -> makeQueueTicketBoxOpen()
            ADQueueType.TICKET_BOX_CLOSE -> makeQueueTicketBoxClose()
        }
        return CuratorQueue(activity = activity, placementAppKey = placementAppKey, sequential = sequential, verifier = verifier, callback = callback)
    }


    // region { TOUCH TICKET }
    private fun makeQueueTouchTicketOpen(): List<CuratorQueueEntity> {
        // interstitial video
        // -> interstitial ssp
        // -> interstitial native
        // -> box banner
        val pid = RemoteContract.touchTicketSetting.pid
        return listOf(
            CuratorQueueEntity(loaderType = ADLoaderType.INTERSTITIAL_VIDEO, placementID = pid.openInterstitialVideoSSP),
            CuratorQueueEntity(loaderType = ADLoaderType.INTERSTITIAL, placementID = pid.openInterstitialSSP),
            CuratorQueueEntity(loaderType = ADLoaderType.INTERSTITIAL_NATIVE, placementID = pid.openInterstitialNative),
            CuratorQueueEntity(loaderType = ADLoaderType.BOX_BANNER, placementID = pid.openBoxBannerSSP)
        )
    }


    private fun makeQueueTouchTicketClose(): List<CuratorQueueEntity> {
        // interstitial ssp
        // -> interstitial native
        val pid = RemoteContract.touchTicketSetting.pid
        return listOf(
            CuratorQueueEntity(loaderType = ADLoaderType.INTERSTITIAL, placementID = pid.closeInterstitialSSP),
            CuratorQueueEntity(loaderType = ADLoaderType.INTERSTITIAL_NATIVE, placementID = pid.closeInterstitialNative)
        )
    }
    // endregion


    // region { VIDEO TICKET }
    private fun makeQueueVideoTicketOpen(): List<CuratorQueueEntity> {
        // reward video
        // -> interstitial ssp
        // -> interstitial native
        // -> box banner
        val pid = RemoteContract.videoTicketSetting.pid
        return listOf(
            CuratorQueueEntity(loaderType = ADLoaderType.REWARD_VIDEO, placementID = pid.openRewardVideoSSP),
            CuratorQueueEntity(loaderType = ADLoaderType.INTERSTITIAL, placementID = pid.openInterstitialSSP),
            CuratorQueueEntity(loaderType = ADLoaderType.INTERSTITIAL_NATIVE, placementID = pid.openInterstitialNative),
            CuratorQueueEntity(loaderType = ADLoaderType.BOX_BANNER, placementID = pid.openBoxBannerSSP)
        )
    }


    private fun makeQueueVideoTicketClose(): List<CuratorQueueEntity> {
        // interstitial ssp
        // -> interstitial native
        val pid = RemoteContract.videoTicketSetting.pid
        return listOf(
            CuratorQueueEntity(loaderType = ADLoaderType.INTERSTITIAL, placementID = pid.closeInterstitialSSP),
            CuratorQueueEntity(loaderType = ADLoaderType.INTERSTITIAL_NATIVE, placementID = pid.closeInterstitialNative)
        )
    }
    // endregion


    // region { TICKET BOX }
    private fun makeQueueTicketBoxOpen(): List<CuratorQueueEntity> {
        // interstitial video
        // -> interstitial ssp
        // -> interstitial native
        // -> box banner
        val pid = RemoteContract.ticketBoxSetting.pid
        return listOf(
            CuratorQueueEntity(loaderType = ADLoaderType.INTERSTITIAL_VIDEO, placementID = pid.openInterstitialVideoSSP),
            CuratorQueueEntity(loaderType = ADLoaderType.INTERSTITIAL, placementID = pid.openInterstitialSSP),
            CuratorQueueEntity(loaderType = ADLoaderType.INTERSTITIAL_NATIVE, placementID = pid.openInterstitialNative),
            CuratorQueueEntity(loaderType = ADLoaderType.BOX_BANNER, placementID = pid.openBoxBannerSSP)
        )
    }


    private fun makeQueueTicketBoxClose(): List<CuratorQueueEntity> {
        // interstitial ssp
        // -> interstitial native
        val pid = RemoteContract.ticketBoxSetting.pid
        return listOf(
            CuratorQueueEntity(loaderType = ADLoaderType.INTERSTITIAL, placementID = pid.closeInterstitialSSP),
            CuratorQueueEntity(loaderType = ADLoaderType.INTERSTITIAL_NATIVE, placementID = pid.closeInterstitialNative)
        )
    }
    // endregion
}