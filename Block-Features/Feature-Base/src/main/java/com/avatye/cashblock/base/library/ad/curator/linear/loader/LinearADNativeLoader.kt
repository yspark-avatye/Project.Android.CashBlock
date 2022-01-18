package com.avatye.cashblock.base.library.ad.curator.linear.loader

import android.content.Context
import android.view.LayoutInflater
import com.avatye.cashblock.base.Core.logger
import com.avatye.cashblock.base.library.ad.curator.ADNetworkType
import com.avatye.cashblock.base.library.ad.curator.Curator
import com.avatye.cashblock.databinding.AcbLibraryAdLayoutNativeLinearBinding
import com.igaworks.ssp.SSPErrorCode
import com.igaworks.ssp.part.nativead.AdPopcornSSPNativeAd
import com.igaworks.ssp.part.nativead.binder.AdPopcornSSPViewBinder
import com.igaworks.ssp.part.nativead.listener.INativeAdEventCallbackListener
import java.lang.ref.WeakReference

internal class LinearADNativeLoader(
    private val context: Context,
    private val placementAppKey: String,
    private val placementID: String,
    private val callback: ILinearADCallback
) : INativeAdEventCallbackListener {

    companion object {
        const val tagName: String = "LinearADNativeLoader"
    }

    private val weakContext = WeakReference(context)
    private val networkName get() = ADNetworkType.from(sspNativeAD?.currentNetwork ?: 0).name
    private val vb: AcbLibraryAdLayoutNativeLinearBinding by lazy {
        AcbLibraryAdLayoutNativeLinearBinding.inflate(LayoutInflater.from(weakContext.get()))
    }

    private var sspNativeAD: AdPopcornSSPNativeAd? = null
    private fun createAdvertiseInstance() {
        if (sspNativeAD == null) {
            sspNativeAD = vb.sspNativeAdView.apply {
                setPlacementId(placementID)
                setPlacementAppKey(placementAppKey)
                setNativeAdEventCallbackListener(this@LinearADNativeLoader)
                // ssp
                adPopcornSSPViewBinder = AdPopcornSSPViewBinder.Builder(vb.nativeLinearIgawContainer.id)
                    .iconImageViewId(vb.nativeLinearIgawIcon.id)
                    .titleViewId(vb.nativeLinearIgawTitle.id)
                    .descViewId(vb.nativeLinearIgawDescription.id)
                    .callToActionId(vb.nativeLinearIgawCta.id)
                    .build()
            }
        }
    }

    private fun initialize(actionCompleteInitialize: () -> Unit) {
        Curator.initSSP(context = context, appKey = placementAppKey) {
            createAdvertiseInstance()
            actionCompleteInitialize()
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