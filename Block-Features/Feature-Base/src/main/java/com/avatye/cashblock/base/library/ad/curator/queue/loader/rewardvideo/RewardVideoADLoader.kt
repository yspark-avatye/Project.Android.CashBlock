package com.avatye.cashblock.base.library.ad.curator.queue.loader.rewardvideo

import android.app.Activity
import com.avatye.cashblock.base.MODULE_NAME
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.base.library.ad.curator.Curator
import com.avatye.cashblock.base.library.ad.curator.ADNetworkType
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
                LogHandler.i(moduleName = MODULE_NAME) { "$tagName -> requestAD -> success { networkName: $networkName }" }
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
        try {
            sspRewardVideoAD?.setRewardVideoAdEventCallbackListener(null)
            sspRewardVideoAD?.destroy()
            sspRewardVideoAD = null
            weakContext.clear()
        } catch (e: Exception) {
            LogHandler.e(moduleName = MODULE_NAME, throwable = e) {
                "$tagName -> release { networkName: $networkName }"
            }
        }
    }

    override fun OnRewardVideoAdLoaded() {
        LogHandler.i(moduleName = MODULE_NAME) {
            "$tagName -> OnRewardVideoAdLoaded { networkName: $networkName }"
        }
        callback.onLoaded()
    }

    override fun OnRewardVideoAdLoadFailed(sspErrorCode: SSPErrorCode?) {
        LogHandler.i(moduleName = MODULE_NAME) {
            "$tagName -> OnRewardVideoAdLoadFailed { code:${sspErrorCode?.errorCode}, message: ${sspErrorCode?.errorMessage}, networkName: $networkName }"
        }
        callback.ondFailed(isBlocked = Curator.isBlocked(sspErrorCode))
    }

    override fun OnRewardVideoAdOpened() {
        LogHandler.i(moduleName = MODULE_NAME) {
            "$tagName -> OnRewardVideoAdOpened { networkName: $networkName }"
        }
        callback.onOpened()
    }

    override fun OnRewardVideoAdOpenFalied() {
        LogHandler.i(moduleName = MODULE_NAME) {
            "$tagName -> OnRewardVideoAdOpenFalied { networkName: $networkName }"
        }
        callback.ondFailed(isBlocked = false)
    }

    override fun OnRewardVideoAdClosed() {
        LogHandler.i(moduleName = MODULE_NAME) {
            "$tagName -> OnRewardVideoAdClosed { networkName: $networkName }"
        }
        callback.onClosed(isCompleted = this.playCompleted)
    }

    override fun OnRewardVideoPlayCompleted(adNetworkNo: Int, completed: Boolean) {
        LogHandler.i(moduleName = MODULE_NAME) {
            "$tagName -> OnRewardVideoPlayCompleted { networkName: $networkName }"
        }
        this.playCompleted = completed
    }
}