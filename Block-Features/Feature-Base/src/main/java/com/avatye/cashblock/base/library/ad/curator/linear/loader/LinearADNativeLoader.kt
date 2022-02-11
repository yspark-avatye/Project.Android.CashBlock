package com.avatye.cashblock.base.library.ad.curator.linear.loader

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.avatye.cashblock.R
import com.avatye.cashblock.base.Core.logger
import com.avatye.cashblock.base.library.ad.curator.ADNetworkType
import com.avatye.cashblock.base.library.ad.curator.Curator
import com.igaworks.ssp.SSPErrorCode
import com.igaworks.ssp.part.nativead.AdPopcornSSPNativeAd
import com.igaworks.ssp.part.nativead.binder.AdPopcornSSPViewBinder
import com.igaworks.ssp.part.nativead.listener.INativeAdEventCallbackListener
import java.lang.ref.WeakReference

internal class LinearADNativeLoader(
    private val context: Context,
    private val placementAppKey: String,
    private val placementID: String,
    private val placementADSize: LinearADSize,
    private val callback: ILinearADCallback
) : INativeAdEventCallbackListener {

    companion object {
        const val tagName: String = "LinearADNativeLoader"
    }

    private val weakContext = WeakReference(context)
    private val networkName get() = ADNetworkType.from(sspNativeAD?.currentNetwork ?: 0).name

    private var sspNativeAD: AdPopcornSSPNativeAd? = null
    private fun createAdvertiseInstance() {
        if (sspNativeAD == null) {
            val nativeLayout = makeNativeLayoutView()
            val nativeADView: AdPopcornSSPNativeAd = nativeLayout.findViewById(R.id.sspNativeAdView)
            sspNativeAD = nativeADView.apply {
                setPlacementId(placementID)
                setPlacementAppKey(placementAppKey)
                setNativeAdEventCallbackListener(this@LinearADNativeLoader)
                // view-binder
                adPopcornSSPViewBinder = AdPopcornSSPViewBinder.Builder(R.id.native_linear_igaw_container).apply {
                    if (placementADSize == LinearADSize.W320XH100) {
                        mainImageViewId(R.id.native_linear_ssp_image)
                    }
                    iconImageViewId(R.id.native_linear_ssp_icon)
                    titleViewId(R.id.native_linear_ssp_title)
                    descViewId(R.id.native_linear_ssp_description)
                    callToActionId(R.id.native_linear_ssp_cta)
                }.build()
            }
        }
    }

    private fun initialize(actionCompleteInitialize: () -> Unit) {
        Curator.initSSP(context = context, appKey = placementAppKey) {
            createAdvertiseInstance()
            actionCompleteInitialize()
        }
    }

    private fun makeNativeLayoutView(): View {
        return when (placementADSize) {
            LinearADSize.W320XH50 -> {
                LayoutInflater.from(weakContext.get()).inflate(R.layout.acb_library_ad_layout_native_linear_320x50, null)
            }
            LinearADSize.W320XH100 -> {
                LayoutInflater.from(weakContext.get()).inflate(R.layout.acb_library_ad_layout_native_linear_320x100, null)
            }
        }
    }

    fun requestAD() {
        initialize {
            sspNativeAD?.loadAd() ?: run {
                logger.i(viewName = tagName) { "requestAD -> fail { loader: null, networkName: $networkName }" }
                callback.onLoadFailed(isBlocked = false)
            }
        }
    }

    fun onResume() {
        // nothing
    }

    fun onPause() {
        // nothing
    }

    fun release() {
        try {
            sspNativeAD?.setNativeAdEventCallbackListener(null)
            sspNativeAD?.removeAllViews()
            sspNativeAD?.destroy()
            sspNativeAD = null
            weakContext.clear()
        } catch (e: Exception) {
            logger.e(viewName = tagName, throwable = e) { "release { networkName: $networkName }" }
        }
    }

    // region { INativeAdEventCallbackListener }
    override fun onNativeAdLoadSuccess() {
        sspNativeAD?.let {
            logger.i(viewName = tagName) { "onNativeAdLoadSuccess -> success { networkName: $networkName }" }
            callback.onLoadSuccess(it)
        } ?: run {
            logger.i(viewName = tagName) { "onNativeAdLoadSuccess -> fail { loader: null, networkName: $networkName }" }
            callback.onLoadFailed(isBlocked = false)
        }
    }

    override fun onImpression() {
        logger.i(viewName = tagName) { "onImpression { networkName: $networkName }" }
    }

    override fun onClicked() {
        logger.i(viewName = tagName) { "onClicked { networkName: $networkName }" }
    }

    override fun onNativeAdLoadFailed(sspErrorCode: SSPErrorCode?) {
        logger.i(viewName = tagName) { "onNativeAdLoadFailed { code: ${sspErrorCode?.errorCode}, message: ${sspErrorCode?.errorMessage}, networkName: $networkName }" }
        callback.onLoadFailed(isBlocked = Curator.isBlocked(sspErrorCode))
    }
    // endregion

}