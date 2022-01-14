package com.avatye.cashblock.base.library.ad.curator.queue.loader.interstitial

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.avatye.cashblock.base.Core.logger
import com.avatye.cashblock.base.library.ad.curator.ADNetworkType
import com.avatye.cashblock.base.library.ad.curator.Curator
import com.avatye.cashblock.base.library.ad.curator.queue.loader.ADLoaderBase
import com.avatye.cashblock.base.library.ad.curator.queue.loader.ADLoaderType
import com.avatye.cashblock.base.library.ad.curator.queue.loader.IADLoaderCallback
import com.igaworks.ssp.SSPErrorCode
import com.igaworks.ssp.part.video.AdPopcornSSPInterstitialVideoAd
import com.igaworks.ssp.part.video.listener.IInterstitialVideoAdEventCallbackListener
import java.lang.ref.WeakReference

internal class InterstitialVideoADLoader(
    private val activity: Activity,
    private val placementAppKey: String,
    private val placementID: String,
    private val callback: IADLoaderCallback
) : ADLoaderBase(), IInterstitialVideoAdEventCallbackListener {

    companion object {
        const val tagName: String = "InterstitialVideoADLoader"
    }

    override val loaderType = ADLoaderType.INTERSTITIAL_VIDEO
    private val weakContext = WeakReference(activity)
    private val networkName: String get() = ADNetworkType.from(sspInterstitialVideoAD?.currentNetwork ?: 0).name

    private val leakHandler = LeakHandler()

    private class LeakHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            // nope
        }
    }

    private var isEvent = false
    private var isEventSend = false
    private var sspInterstitialVideoAD: AdPopcornSSPInterstitialVideoAd? = null
    private fun createAdvertiseInstance() {
        if (sspInterstitialVideoAD == null) {
            sspInterstitialVideoAD = AdPopcornSSPInterstitialVideoAd(weakContext.get()).apply {
                this.setPlacementId(placementID)
                this.setPlacementAppKey(placementAppKey)
                this.setNetworkScheduleTimeout(Curator.VideoNetworkScheduleTimeout)
                this.setEventCallbackListener(this@InterstitialVideoADLoader)
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
            sspInterstitialVideoAD?.let {
                it.loadAd()
                leakHandler.postDelayed({
                    if (!isEvent) {
                        logger.e(viewName = tagName) { "requestAD -> fail { timeOver: 15sec, networkName: $networkName }" }
                        isEventSend = true
                        callback.ondFailed(isBlocked = false)
                    }
                }, 15000)
            } ?: run {
                logger.e(viewName = tagName) { "requestAD -> fail { loader: null, networkName: $networkName }" }
                OnInterstitialVideoAdOpenFalied()
            }
        }
    }

    override fun show(action: (success: Boolean) -> Unit) {
        if (sspInterstitialVideoAD?.isReady == true) {
            sspInterstitialVideoAD?.showAd()
            action(true)
        } else {
            action(false)
        }
    }

    override fun onResume() {
        sspInterstitialVideoAD?.onResume()
    }

    override fun onPause() {
        sspInterstitialVideoAD?.onPause()
    }

    override fun release() {
        kotlin.runCatching {
            sspInterstitialVideoAD?.setEventCallbackListener(null)
            sspInterstitialVideoAD?.destroy()
            sspInterstitialVideoAD = null
            weakContext.clear()
        }.onFailure {
            logger.e(viewName = tagName, throwable = it) { "release { networkName: $networkName }" }
        }
    }

    // region { IInterstitialVideoAdEventCallbackListener }
    override fun OnInterstitialVideoAdLoaded() {
        isEvent = true
        if (!isEventSend) {
            logger.i(viewName = tagName) { "OnInterstitialVideoAdLoaded { networkName: $networkName }" }
            callback.onLoaded()
        }
    }

    override fun OnInterstitialVideoAdLoadFailed(sspErrorCode: SSPErrorCode?) {
        isEvent = true
        if (!isEventSend) {
            logger.i(viewName = tagName) {
                "OnInterstitialVideoAdLoadFailed { pid:$placementID, code:${sspErrorCode?.errorCode}, message:${sspErrorCode?.errorMessage}, networkName: $networkName }"
            }
            callback.ondFailed(isBlocked = Curator.isBlocked(sspErrorCode))
        }
    }

    override fun OnInterstitialVideoAdOpened() {
        isEvent = true
        // callback
        if (!isEventSend) {
            logger.i(viewName = tagName) { "OnInterstitialVideoAdOpened { networkName: $networkName }" }
            callback.onOpened()
        }
    }

    override fun OnInterstitialVideoAdOpenFalied() {
        isEvent = true
        if (!isEventSend) {
            logger.i(viewName = tagName) { "OnInterstitialVideoAdOpenFalied { networkName: $networkName }" }
            callback.ondFailed(isBlocked = false)
        }
    }

    override fun OnInterstitialVideoAdClosed() {
        isEvent = true
        if (!isEventSend) {
            logger.i(viewName = tagName) { "OnInterstitialVideoAdClosed { networkName: $networkName }" }
            callback.onClosed(isCompleted = true)
        }
    }
    // endregion

}