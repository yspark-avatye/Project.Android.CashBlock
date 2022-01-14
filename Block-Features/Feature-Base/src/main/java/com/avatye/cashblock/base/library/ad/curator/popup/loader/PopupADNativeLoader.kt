package com.avatye.cashblock.base.library.ad.curator.popup.loader

import android.content.Context
import android.view.LayoutInflater
import com.avatye.cashblock.base.Core.logger
import com.avatye.cashblock.base.library.ad.curator.ADNetworkType
import com.avatye.cashblock.base.library.ad.curator.Curator
import com.avatye.cashblock.databinding.AcbLibraryAdLayoutNativePopupBinding
import com.igaworks.ssp.SSPErrorCode
import com.igaworks.ssp.part.nativead.AdPopcornSSPNativeAd
import com.igaworks.ssp.part.nativead.binder.AdPopcornSSPViewBinder
import com.igaworks.ssp.part.nativead.listener.INativeAdEventCallbackListener
import java.lang.ref.WeakReference

internal class PopupADNativeLoader(
    private val context: Context,
    private val placementAppKey: String,
    private val placementID: String,
    private val callback: IPopupADCallback
) : INativeAdEventCallbackListener {

    companion object {
        const val tagName: String = "PopupADNativeLoader"
    }

    private val weakContext = WeakReference(context)
    private val networkName: String get() = ADNetworkType.from(sspNativeAD?.currentNetwork ?: 0).name
    private val vb: AcbLibraryAdLayoutNativePopupBinding by lazy {
        AcbLibraryAdLayoutNativePopupBinding.inflate(LayoutInflater.from(weakContext.get()))
    }

    private var sspNativeAD: AdPopcornSSPNativeAd? = null
    private fun createAdvertiseInstance() {
        if (sspNativeAD == null) {
            sspNativeAD = vb.sspNativeAdView.apply {
                setPlacementId(placementID)
                setPlacementAppKey(placementAppKey)
                setNativeAdEventCallbackListener(this@PopupADNativeLoader)
                // ssp
                adPopcornSSPViewBinder = AdPopcornSSPViewBinder.Builder(vb.nativePopupSspContainer.id)
                    .mainImageViewId(vb.nativePopupSspImage.id)
                    .iconImageViewId(vb.nativePopupSspIcon.id)
                    .titleViewId(vb.nativePopupSspTitle.id)
                    .descViewId(vb.nativePopupSspDescription.id)
                    .callToActionId(vb.nativePopupSspCta.id)
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
                logger.i(viewName = PopupADLoader.tagName) { "requestAD -> fail { loader: null, networkName: $networkName }" }
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
        kotlin.runCatching {
            sspNativeAD?.setNativeAdEventCallbackListener(null)
            sspNativeAD?.removeAllViews()
            sspNativeAD?.destroy()
            sspNativeAD = null
            weakContext.clear()
        }.onFailure {
            logger.e(viewName = tagName, throwable = it) { "release" }
        }
    }

    // region { INativeAdEventCallbackListener }
    override fun onNativeAdLoadSuccess() {
        sspNativeAD?.let {
            logger.i(viewName = PopupADLoader.tagName) { "onNativeAdLoadSuccess -> success { networkName: $networkName }" }
            callback.onLoadSuccess(view = it, currentNetwork = sspNativeAD?.currentNetwork ?: 0)
        } ?: run {
            logger.i(viewName = PopupADLoader.tagName) { "onNativeAdLoadSuccess -> fail { loader: null, networkName: $networkName }" }
            callback.onLoadFailed(isBlocked = false)
        }
    }

    override fun onImpression() {
        logger.i(viewName = PopupADLoader.tagName) { "onImpression { networkName: $networkName }" }
    }

    override fun onClicked() {
        logger.i(viewName = PopupADLoader.tagName) { "onClicked { networkName: $networkName }" }
    }

    override fun onNativeAdLoadFailed(sspErrorCode: SSPErrorCode?) {
        logger.i(viewName = PopupADLoader.tagName) {
            "onNativeAdLoadFailed { pid: ${placementID}, code: ${sspErrorCode?.errorCode}, message: ${sspErrorCode?.errorMessage}, networkName: $networkName }"
        }
        callback.onLoadFailed(isBlocked = Curator.isBlocked(sspErrorCode))
    }
    // endregion
}