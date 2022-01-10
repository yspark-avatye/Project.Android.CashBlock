package com.avatye.cashblock.base.library.ad.curator.queue.loader.interstitial

import android.app.Activity
import android.graphics.Color
import com.avatye.cashblock.base.MODULE_NAME
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.base.library.ad.curator.Curator
import com.avatye.cashblock.base.library.ad.curator.ADNetworkType
import com.avatye.cashblock.base.library.ad.curator.queue.loader.ADLoaderBase
import com.avatye.cashblock.base.library.ad.curator.queue.loader.ADLoaderType
import com.avatye.cashblock.base.library.ad.curator.queue.loader.IADLoaderCallback
import com.igaworks.ssp.SSPErrorCode
import com.igaworks.ssp.part.interstitial.AdPopcornSSPInterstitialAd
import com.igaworks.ssp.part.interstitial.listener.IInterstitialEventCallbackListener
import java.lang.ref.WeakReference

internal class InterstitialADLoader(
    private val activity: Activity,
    private val placementAppKey: String,
    private val placementID: String,
    private val callback: IADLoaderCallback
) : ADLoaderBase(), IInterstitialEventCallbackListener {

    companion object {
        const val tagName: String = "InterstitialADLoader"
    }

    private val weakActivity = WeakReference(activity)
    override val loaderType = ADLoaderType.INTERSTITIAL
    private val networkName: String get() = ADNetworkType.from(sspInterstitialAD?.currentNetwork ?: 0).name

    private var sspInterstitialAD: AdPopcornSSPInterstitialAd? = null
    private fun createAdvertiseInstance() {
        if (sspInterstitialAD == null) {
            sspInterstitialAD = AdPopcornSSPInterstitialAd(weakActivity.get()).apply {
                this.setPlacementId(placementID)
                this.setPlacementAppKey(placementAppKey)
                this.setCurrentActivity(weakActivity.get())
                val extras: HashMap<String, Any> = hashMapOf(
                    AdPopcornSSPInterstitialAd.CustomExtraData.APSSP_AD_DISABLE_BACK_BTN to true,
                    AdPopcornSSPInterstitialAd.CustomExtraData.IS_ENDING_AD to false,
                    AdPopcornSSPInterstitialAd.CustomExtraData.APSSP_AD_BACKGROUND_COLOR to Color.parseColor("#4C000000")
                )
                this.setCustomExtras(extras)
                this.setInterstitialEventCallbackListener(this@InterstitialADLoader)
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
            sspInterstitialAD?.loadAd() ?: run {
                LogHandler.i(moduleName = MODULE_NAME) {
                    "$tagName -> requestAD -> fail { loader: null, networkName: $networkName }"
                }
                callback.ondFailed(isBlocked = false)
            }
        }
    }

    override fun show(action: (success: Boolean) -> Unit) {
        if (sspInterstitialAD?.isLoaded == true) {
            sspInterstitialAD?.showAd()
            action(true)
        } else {
            action(false)
        }
    }

    override fun onResume() {
        sspInterstitialAD?.onResume()
    }

    override fun onPause() {
        // nope
    }

    override fun release() {
        try {
            sspInterstitialAD?.setInterstitialEventCallbackListener(null)
            sspInterstitialAD?.destroy()
            sspInterstitialAD = null
            weakActivity.clear()
        } catch (e: Exception) {
            LogHandler.i(moduleName = MODULE_NAME, throwable = e) {
                "$tagName -> release { networkName: $networkName }"
            }
        }
    }

    // region { IInterstitialEventCallbackListener }
    override fun OnInterstitialLoaded() {
        LogHandler.i(moduleName = MODULE_NAME) {
            "$tagName -> OnInterstitialLoaded { networkName: $networkName }"
        }
        callback.onLoaded()
    }

    override fun OnInterstitialReceiveFailed(sspErrorCode: SSPErrorCode?) {
        LogHandler.i(moduleName = MODULE_NAME) {
            "$tagName -> OnInterstitialReceiveFailed { pid: $placementID, code: ${sspErrorCode?.errorCode}, message: ${sspErrorCode?.errorMessage}, networkName: $networkName }"
        }
        callback.ondFailed(isBlocked = Curator.isBlocked(sspErrorCode))
    }

    override fun OnInterstitialOpened() {
        LogHandler.i(moduleName = MODULE_NAME) {
            "$tagName -> OnInterstitialOpened { networkName: $networkName }"
        }
        callback.onOpened()
    }

    override fun OnInterstitialOpenFailed(sspErrorCode: SSPErrorCode?) {
        LogHandler.i(moduleName = MODULE_NAME) {
            "$tagName -> OnInterstitialOpenFailed { pid: $placementID, code: ${sspErrorCode?.errorCode}, message: ${sspErrorCode?.errorMessage}, networkName: $networkName }"
        }
        callback.ondFailed(isBlocked = Curator.isBlocked(sspErrorCode))
    }

    /**
     * // UNKNOWN = 0;
     * // CLICK_CLOSE_BTN = 1;
     * // PRESSED_BACK_KEY = 2;
     * // SWIPE_RIGHT_TO_LEFT = 3;
     * // SWIPE_LEFT_TO_RIGHT = 4;
     * // AUTO_CLOSE = 5;
     *
     * @param reason reason
     */
    override fun OnInterstitialClosed(reason: Int) {
        LogHandler.i(moduleName = MODULE_NAME) {
            "$tagName -> OnInterstitialClosed { reason: $reason, networkName: $networkName }"
        }
        callback.onClosed(isCompleted = true)
    }

    override fun OnInterstitialClicked() {
        LogHandler.i(moduleName = MODULE_NAME) {
            "$tagName -> OnInterstitialClicked { networkName: $networkName }"
        }
    }
    // endregion
}