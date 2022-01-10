package com.avatye.cashblock.base.library.ad.curator.linear

import android.content.Context
import android.view.View
import com.avatye.cashblock.base.MODULE_NAME
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.base.library.ad.curator.IADAgeVerifier
import com.avatye.cashblock.base.library.ad.curator.linear.loader.ILinearADCallback
import com.avatye.cashblock.base.library.ad.curator.linear.loader.LinearADLoader
import com.avatye.cashblock.base.library.ad.curator.linear.loader.LinearADNativeLoader


class CuratorLinear(
    private val context: Context,
    private val placementAppKey: String,
    private val sspPlacementID: String,
    private val nativePlacementID: String,
    private val mediationExtraData: HashMap<String, Any>? = null,
    private val verifier: IADAgeVerifier,
    private val callback: ICuratorLinearCallback
) {

    private val tagName: String = "CuratorLinear"

    private var sspLoader: LinearADLoader? = null
    private var nativeLoader: LinearADNativeLoader? = null

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
            // native
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
            sspLoader = LinearADLoader(
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
            nativeLoader = LinearADNativeLoader(
                context = context,
                placementAppKey = placementAppKey,
                placementID = nativePlacementID,
                callback = CallbackNativeAD()
            )
        }
        nativeLoader?.requestAD() ?: callback.onFailure(isBlocked = false)
    }

    // region { ILinearADCallback - SSP }
    private inner class CallbackSSPAD : ILinearADCallback {
        override fun onLoadSuccess(view: View) {
            LogHandler.i(moduleName = MODULE_NAME) {
                "$tagName -> SSP -> onLoadSuccess"
            }
            callback.oSuccess(view)
        }

        override fun onLoadFailed(isBlocked: Boolean) {
            LogHandler.i(moduleName = MODULE_NAME) {
                "$tagName -> SSP -> onLoadFailed { isBlocked: $isBlocked, placementID: $sspPlacementID }"
            }
            if (isBlocked) {
                callback.onFailure(isBlocked = isBlocked)
            } else {
                requestNative()
            }
        }
    }
    // endregion

    // region { ILinearADCallback - Native }
    private inner class CallbackNativeAD : ILinearADCallback {
        override fun onLoadSuccess(view: View) {
            LogHandler.i(moduleName = MODULE_NAME) {
                "$tagName -> Native -> onLoadSuccess"
            }
            callback.oSuccess(view)
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