package com.avatye.cashblock.base.library.ad.curator.popup

import android.content.Context
import android.view.View
import com.avatye.cashblock.base.MODULE_NAME
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.base.library.ad.curator.ADNetworkType
import com.avatye.cashblock.base.library.ad.curator.IADAgeVerifier
import com.avatye.cashblock.base.library.ad.curator.popup.loader.IPopupADCallback
import com.avatye.cashblock.base.library.ad.curator.popup.loader.PopupADLoader
import com.avatye.cashblock.base.library.ad.curator.popup.loader.PopupADNativeLoader

class CuratorPopup(
    private val context: Context,
    private val placementAppKey: String,
    private val sspPlacementID: String,
    private val nativePlacementID: String,
    private val mediationExtraData: HashMap<String, Any>? = null,
    private val verifier: IADAgeVerifier,
    private val callback: ICuratorPopupCallback
) {
    companion object {
        const val tagName: String = "CuratorPopup"
    }

    private var sspLoader: PopupADLoader? = null
    private var nativeLoader: PopupADNativeLoader? = null

    fun requestAD() {
        if (verifier.isVerified()) {
            requestSSP()
        } else {
            callback.onNeedAgeVerification()
        }
    }

    fun release() {
        try {
            // log
            LogHandler.i(moduleName = MODULE_NAME) {
                "$tagName -> release -> success"
            }
            // ssp
            sspLoader?.release()
            sspLoader = null
            // ssp - native
            nativeLoader?.release()
            nativeLoader = null
        } catch (e: Exception) {
            LogHandler.e(moduleName = MODULE_NAME, throwable = e) {
                "$tagName -> release"
            }
        }
    }

    fun onResume() {
        sspLoader?.onResume()
        nativeLoader?.onResume()
    }

    fun onPause() {
        sspLoader?.onPause()
        nativeLoader?.onPause()
    }

    private fun requestSSP() {
        if (sspPlacementID.isNotEmpty()) {
            sspLoader = PopupADLoader(
                context = context,
                placementAppKey = placementAppKey,
                placementID = sspPlacementID,
                mediationExtraData = mediationExtraData,
                callback = CallbackSSPAD()
            )
        }
        sspLoader?.requestAD() ?: requestNative()
    }

    private fun requestNative() {
        if (nativePlacementID.isNotEmpty()) {
            nativeLoader = PopupADNativeLoader(
                context = context,
                placementAppKey = placementAppKey,
                placementID = nativePlacementID,
                callback = CallbackNativeAD()
            )
        }
        nativeLoader?.requestAD() ?: callback.onFailure(isBlocked = false)
    }

    // region { IPopupADCallback - SSP }
    inner class CallbackSSPAD : IPopupADCallback {
        override fun onLoadSuccess(view: View, currentNetwork: Int) {
            LogHandler.i(moduleName = MODULE_NAME) {
                "$tagName -> SSP -> onLoadSuccess"
            }
            callback.oSuccess(adView = view, network = ADNetworkType.from(currentNetwork))
        }

        override fun onLoadFailed(isBlocked: Boolean) {
            LogHandler.i(moduleName = MODULE_NAME) {
                "$tagName -> SSP -> onLoadFailed { isBlocked: $isBlocked, placementID: $sspPlacementID } "
            }
            if (isBlocked) {
                callback.onFailure(isBlocked = true)
            } else {
                requestNative()
            }
        }
    }
    // endregion

    // region { IPopupADCallback - Native }
    inner class CallbackNativeAD : IPopupADCallback {
        override fun onLoadSuccess(view: View, currentNetwork: Int) {
            LogHandler.i(moduleName = MODULE_NAME) {
                "$tagName -> Native -> onLoadSuccess"
            }
            callback.oSuccess(adView = view, network = ADNetworkType.from(currentNetwork))
        }

        override fun onLoadFailed(isBlocked: Boolean) {
            LogHandler.i(moduleName = MODULE_NAME) {
                "$tagName -> Native -> onLoadFailed { isBlocked: $isBlocked, placementID: $nativePlacementID }"
            }
            callback.onFailure(isBlocked = isBlocked)
        }
    }
    // endregion
}