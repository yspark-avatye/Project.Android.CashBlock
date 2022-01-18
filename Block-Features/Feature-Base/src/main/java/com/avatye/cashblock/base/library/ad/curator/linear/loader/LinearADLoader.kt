package com.avatye.cashblock.base.library.ad.curator.linear.loader

import android.content.Context
import com.avatye.cashblock.base.Core.logger
import com.avatye.cashblock.base.library.ad.curator.ADNetworkType
import com.avatye.cashblock.base.library.ad.curator.Curator
import com.igaworks.ssp.BannerAnimType
import com.igaworks.ssp.SSPErrorCode
import com.igaworks.ssp.part.banner.AdPopcornSSPBannerAd
import com.igaworks.ssp.part.banner.listener.IBannerEventCallbackListener
import java.lang.ref.WeakReference

internal class LinearADLoader(
    private val context: Context,
    private val placementAppKey: String,
    private val placementID: String,
    private val mediationExtraData: HashMap<String, Any>? = null,
    private val callback: ILinearADCallback
) : IBannerEventCallbackListener {

    private val tagName: String = "LinearADLoader"

    private val weakContext = WeakReference(context)
    private val networkName: String
        get() {
            return ADNetworkType.from(sspBannerAD?.currentNetwork ?: 0).name
        }

    private var sspBannerAD: AdPopcornSSPBannerAd? = null
    private fun createAdvertiseInstance() {
        if (sspBannerAD == null) {
            sspBannerAD = AdPopcornSSPBannerAd(weakContext.get()).apply {
                this.autoBgColor = false
                this.placementId = placementID
                this.setPlacementAppKey(placementAppKey)
                this.setAdSize(com.igaworks.ssp.AdSize.BANNER_320x50)
                this.setBannerAnimType(BannerAnimType.NONE)
                this.setRefreshTime(Curator.RefreshTime)
                this.setNetworkScheduleTimeout(Curator.NetworkScheduleTimeout)
                this.setBannerEventCallbackListener(this@LinearADLoader)
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
                logger.i(viewName = tagName) { "requestLoad -> fail { loader: null, networkName: $networkName }" }
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
        try {
            sspBannerAD?.setBannerEventCallbackListener(null)
            sspBannerAD?.removeAllViews()
            sspBannerAD?.stopAd()
            sspBannerAD = null
            weakContext.clear()
        } catch (e: Exception) {
            logger.e(viewName = tagName, throwable = e) { "release { networkName: $networkName }" }
        }
    }

    // region { IInterstitialVideoAdEventCallbackListener }
    override fun OnBannerAdReceiveSuccess() {
        sspBannerAD?.let {
            logger.i(viewName = tagName) { "OnBannerAdReceiveSuccess -> success { networkName: $networkName }" }
            callback.onLoadSuccess(it)
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