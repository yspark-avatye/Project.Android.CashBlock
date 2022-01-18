package com.avatye.cashblock.base.library.ad.curator.popup.loader

import android.content.Context
import com.avatye.cashblock.base.Core.logger
import com.avatye.cashblock.base.library.ad.curator.ADNetworkType
import com.avatye.cashblock.base.library.ad.curator.Curator
import com.igaworks.ssp.BannerAnimType
import com.igaworks.ssp.SSPErrorCode
import com.igaworks.ssp.part.banner.AdPopcornSSPBannerAd
import com.igaworks.ssp.part.banner.listener.IBannerEventCallbackListener
import java.lang.ref.WeakReference

internal class PopupADLoader(
    private val context: Context,
    private val placementAppKey: String,
    private val placementID: String,
    private val mediationExtraData: HashMap<String, Any>? = null,
    private val callback: IPopupADCallback
) : IBannerEventCallbackListener {

    companion object {
        const val tagName: String = "PopupADLoader"
    }

    private val weakContext = WeakReference(context)
    private val networkName: String get() = ADNetworkType.from(sspBannerAD?.currentNetwork ?: 0).name

    private var sspBannerAD: AdPopcornSSPBannerAd? = null
    private fun createAdvertiseInstance() {
        if (sspBannerAD == null) {
            sspBannerAD = AdPopcornSSPBannerAd(weakContext.get()).apply {
                this.autoBgColor = false
                this.placementId = placementID
                this.setPlacementAppKey(placementAppKey)
                this.setAdSize(com.igaworks.ssp.AdSize.BANNER_300x250)
                this.setBannerAnimType(BannerAnimType.NONE)
                this.setRefreshTime(Curator.RefreshTime)
                this.setNetworkScheduleTimeout(Curator.NetworkScheduleTimeout)
                this.setBannerEventCallbackListener(this@PopupADLoader)
                mediationExtraData?.let { this.setMediationExtras(it) }
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
            sspBannerAD?.loadAd() ?: run {
                logger.i(viewName = tagName) { "requestAD -> fail { loader: null, networkName: $networkName }" }
                callback.onLoadFailed(isBlocked = false)
            }
        }
    }

    fun onResume() {
        sspBannerAD?.onResume()
    }

    fun onPause() {
        sspBannerAD?.onPause()
    }

    fun release() {
        kotlin.runCatching {
            sspBannerAD?.setBannerEventCallbackListener(null)
            sspBannerAD?.removeAllViews()
            sspBannerAD?.stopAd()
            sspBannerAD = null
            weakContext.clear()
        }.onFailure {
            logger.e(viewName = tagName, throwable = it) { "release { networkName: $networkName }" }
        }
    }

    // region { IBannerEventCallbackListener }
    override fun OnBannerAdReceiveSuccess() {
        sspBannerAD?.let {
            logger.i(viewName = tagName) { "OnBannerAdReceiveSuccess -> success{ networkName: $networkName }" }
            callback.onLoadSuccess(view = it, currentNetwork = sspBannerAD?.currentNetwork ?: 0)
        } ?: run {
            logger.i(viewName = tagName) { "OnBannerAdReceiveSuccess -> fail { loader: null, networkName: $networkName }" }
            callback.onLoadFailed(isBlocked = false)
        }
    }

    override fun OnBannerAdReceiveFailed(sspErrorCode: SSPErrorCode?) {
        logger.i(viewName = tagName) { "OnBannerAdReceiveFailed { code: ${sspErrorCode?.errorCode}, message: ${sspErrorCode?.errorMessage}, networkName: $networkName }" }
        callback.onLoadFailed(isBlocked = Curator.isBlocked(sspErrorCode))
    }

    override fun OnBannerAdClicked() {
        logger.i(viewName = tagName) { "OnBannerAdClicked { networkName: $networkName }" }
    }
    // endregion
}