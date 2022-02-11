package com.avatye.cashblock.base.library.ad.curator.linear

import android.content.Context
import android.view.View
import com.avatye.cashblock.base.Core.logger
import com.avatye.cashblock.base.library.ad.curator.IADAgeVerifier
import com.avatye.cashblock.base.library.ad.curator.linear.loader.ILinearADCallback
import com.avatye.cashblock.base.library.ad.curator.linear.loader.LinearADLoader
import com.avatye.cashblock.base.library.ad.curator.linear.loader.LinearADNativeLoader
import com.avatye.cashblock.base.library.ad.curator.linear.loader.LinearADSize


class CuratorLinear(
    private val context: Context,
    private val placementAppKey: String,
    private val placementADSize: LinearADSize,
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
        kotlin.runCatching {
            // log
            logger.i(viewName = tagName) { "release -> success" }
            // ssp
            sspLoader?.release()
            sspLoader = null
            // native
            nativeLoader?.release()
            nativeLoader = null
        }.onFailure {
            logger.e(viewName = tagName, throwable = it) { "release" }
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
                placementADSize = placementADSize,
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
                placementADSize = placementADSize,
                placementID = nativePlacementID,
                callback = CallbackNativeAD()
            )
        }
        nativeLoader?.requestAD() ?: callback.onFailure(isBlocked = false)
    }

    // region { ILinearADCallback - SSP }
    private inner class CallbackSSPAD : ILinearADCallback {
        override fun onLoadSuccess(view: View) {
            logger.i(viewName = tagName) { "SSP -> onLoadSuccess" }
            callback.oSuccess(view)
        }

        override fun onLoadFailed(isBlocked: Boolean) {
            logger.i(viewName = tagName) { "SSP -> onLoadFailed { isBlocked: $isBlocked, placementID: $sspPlacementID }" }
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
            logger.i(viewName = tagName) { "Native -> onLoadSuccess" }
            callback.oSuccess(view)
        }

        override fun onLoadFailed(isBlocked: Boolean) {
            logger.i(viewName = tagName) { "Native -> onLoadFailed { isBlocked: $isBlocked, placementID: $nativePlacementID }" }
            callback.onFailure(isBlocked = isBlocked)
        }
    }
    // endregion
}