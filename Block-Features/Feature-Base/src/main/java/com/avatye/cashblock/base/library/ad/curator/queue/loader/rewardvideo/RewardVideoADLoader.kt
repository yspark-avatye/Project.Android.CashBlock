package com.avatye.cashblock.base.library.ad.curator.queue.loader.rewardvideo

import android.app.Activity
import com.avatye.cashblock.base.Core.logger
import com.avatye.cashblock.base.library.ad.curator.ADNetworkType
import com.avatye.cashblock.base.library.ad.curator.Curator
import com.avatye.cashblock.base.library.ad.curator.queue.loader.ADLoaderBase
import com.avatye.cashblock.base.library.ad.curator.queue.loader.ADLoaderType
import com.avatye.cashblock.base.library.ad.curator.queue.loader.IADLoaderCallback
import com.igaworks.ssp.SSPErrorCode
import com.igaworks.ssp.part.video.AdPopcornSSPRewardVideoAd
import com.igaworks.ssp.part.video.listener.IRewardVideoAdEventCallbackListener
import java.lang.ref.WeakReference

internal class RewardVideoADLoader(
    private val activity: Activity,
    private val placementAppKey: String,
    private val placementID: String,
    private val callback: IADLoaderCallback
) : ADLoaderBase(), IRewardVideoAdEventCallbackListener {

    companion object {
        const val tagName: String = "RewardVideoADLoader"
    }

    override val loaderType: ADLoaderType = ADLoaderType.REWARD_VIDEO
    private val weakContext = WeakReference(activity)
    private val networkName: String get() = ADNetworkType.from(sspRewardVideoAD?.currentNetwork ?: 0).name

    private var playCompleted: Boolean = false
    private var sspRewardVideoAD: AdPopcornSSPRewardVideoAd? = null
    private fun createAdvertiseInstance() {
        if (sspRewardVideoAD == null) {
            sspRewardVideoAD = AdPopcornSSPRewardVideoAd(weakContext.get()).apply {
                this.setPlacementId(placementID)
                this.setPlacementAppKey(placementAppKey)
                this.setNetworkScheduleTimeout(Curator.VideoNetworkScheduleTimeout)
                this.setRewardVideoAdEventCallbackListener(this@RewardVideoADLoader)
            }
        }
    }

    private fun initialize(actionCompleteInitialize: () -> Unit) {
        Curator.initSSP(context = activity, appKey = placementAppKey) {
            createAdvertiseInstance()
            actionCompleteInitialize()
        }
    }

    override fun requestAD() {
        initialize {
            playCompleted = false
            sspRewardVideoAD?.loadAd() ?: run {
                logger.i(viewName = tagName) { "requestAD -> success { networkName: $networkName }" }
                callback.ondFailed(isBlocked = false)
            }
        }
    }

    override fun show(action: (success: Boolean) -> Unit) {
        sspRewardVideoAD?.let {
            if (it.isReady) {
                it.showAd()
                action(true)
            } else {
                action(false)
            }
        } ?: run {
            action(false)
        }
    }

    override fun onResume() {
        sspRewardVideoAD?.onResume()
    }

    override fun onPause() {
        sspRewardVideoAD?.onPause()
    }

    override fun release() {
        kotlin.runCatching {
            sspRewardVideoAD?.setRewardVideoAdEventCallbackListener(null)
            sspRewardVideoAD?.destroy()
            sspRewardVideoAD = null
            weakContext.clear()
        }.onFailure {
            logger.e(viewName = tagName, throwable = it) { "release { networkName: $networkName }" }
        }
    }

    override fun OnRewardVideoAdLoaded() {
        logger.i(viewName = tagName) { "OnRewardVideoAdLoaded { networkName: $networkName }" }
        callback.onLoaded()
    }

    override fun OnRewardVideoAdLoadFailed(sspErrorCode: SSPErrorCode?) {
        logger.i(viewName = tagName) {
            "OnRewardVideoAdLoadFailed { code:${sspErrorCode?.errorCode}, message: ${sspErrorCode?.errorMessage}, networkName: $networkName }"
        }
        callback.ondFailed(isBlocked = Curator.isBlocked(sspErrorCode))
    }

    override fun OnRewardVideoAdOpened() {
        logger.i(viewName = tagName) { "OnRewardVideoAdOpened { networkName: $networkName }" }
        callback.onOpened()
    }

    override fun OnRewardVideoAdOpenFalied() {
        logger.i(viewName = tagName) { "OnRewardVideoAdOpenFalied { networkName: $networkName }" }
        callback.ondFailed(isBlocked = false)
    }

    override fun OnRewardVideoAdClosed() {
        logger.i(viewName = tagName) { "OnRewardVideoAdClosed { networkName: $networkName }" }
        callback.onClosed(isCompleted = this.playCompleted)
    }

    override fun OnRewardVideoPlayCompleted(adNetworkNo: Int, completed: Boolean) {
        logger.i(viewName = tagName) { "OnRewardVideoPlayCompleted { networkName: $networkName }" }
        this.playCompleted = completed
    }
}