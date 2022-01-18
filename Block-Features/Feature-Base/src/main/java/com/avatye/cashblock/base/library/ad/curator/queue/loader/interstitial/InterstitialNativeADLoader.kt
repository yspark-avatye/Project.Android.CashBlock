package com.avatye.cashblock.base.library.ad.curator.queue.loader.interstitial

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import com.avatye.cashblock.R
import com.avatye.cashblock.base.Core.logger
import com.avatye.cashblock.base.MODULE_NAME
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.base.library.ad.curator.ADNetworkType
import com.avatye.cashblock.base.library.ad.curator.Curator
import com.avatye.cashblock.base.library.ad.curator.queue.loader.ADLoaderBase
import com.avatye.cashblock.base.library.ad.curator.queue.loader.ADLoaderType
import com.avatye.cashblock.base.library.ad.curator.queue.loader.IADLoaderCallback
import com.avatye.cashblock.databinding.AcbLibraryAdLayoutNativeInterstitialBinding
import com.igaworks.ssp.SSPErrorCode
import com.igaworks.ssp.part.nativead.AdPopcornSSPNativeAd
import com.igaworks.ssp.part.nativead.binder.*
import com.igaworks.ssp.part.nativead.listener.INativeAdEventCallbackListener
import java.lang.ref.WeakReference

internal class InterstitialNativeADLoader(
    private val activity: Activity,
    private val placementAppKey: String,
    private val placementID: String,
    private val callback: IADLoaderCallback
) : ADLoaderBase(), INativeAdEventCallbackListener {

    companion object {
        const val tagName: String = "InterstitialNativeADLoader"
    }

    private val weakActivity = WeakReference(activity)
    override val loaderType: ADLoaderType = ADLoaderType.INTERSTITIAL_NATIVE
    private val networkName: String
        get() {
            return ADNetworkType.from(sspNativeAD?.currentNetwork ?: 0).name
        }

    private val vb: AcbLibraryAdLayoutNativeInterstitialBinding by lazy {
        AcbLibraryAdLayoutNativeInterstitialBinding.inflate(LayoutInflater.from(weakActivity.get()))
    }

    private var sspNativeAD: AdPopcornSSPNativeAd? = null
    private fun createAdvertiseInstance() {
        if (sspNativeAD == null) {
            sspNativeAD = vb.sspNativeAdView.apply {
                this.setPlacementId(placementID)
                this.setPlacementAppKey(placementAppKey)
                this.setNativeAdEventCallbackListener(this@InterstitialNativeADLoader)
                // ssp
                adPopcornSSPViewBinder = AdPopcornSSPViewBinder.Builder(vb.nativeInterstitialIgawContainer.id)
                    .mainImageViewId(vb.nativeInterstitialIgawImage.id)
                    .iconImageViewId(vb.nativeInterstitialIgawIcon.id)
                    .titleViewId(vb.nativeInterstitialIgawTitle.id)
                    .descViewId(vb.nativeInterstitialIgawDescription.id)
                    .callToActionId(vb.nativeInterstitialIgawCta.id)
                    .build()
                // mediation:admob
                kotlin.runCatching {
                    Class.forName("com.google.android.gms.ads.nativead.NativeAdView")
                }.onSuccess {
                    logger.i(viewName = tagName) { "nativeAD { admob: imported }" }
                    vb.nativeInterstitialAdmobStub.inflate()
                    adMobViewBinder = AdMobViewBinder.Builder(
                        R.id.native_interstitial_admob_container,
                        R.layout.acb_library_ad_stub_admob_native_interstitial
                    )
                        .iconViewId(R.id.native_interstitial_admob_icon)
                        .headlineViewId(R.id.native_interstitial_admob_headline)
                        .bodyViewId(R.id.native_interstitial_admob_body)
                        .mediaViewId(R.id.native_interstitial_admob_media)
                        .callToActionId(R.id.native_interstitial_admob_cta)
                        .advertiserViewId(R.id.native_interstitial_admob_advertiser)
                        .priceViewId(R.id.native_interstitial_admob_price)
                        .starRatingViewId(R.id.native_interstitial_admob_stars)
                        .storeViewId(R.id.native_interstitial_admob_store)
                        .build()

                }.onFailure { t ->
                    logger.e(viewName = tagName, throwable = t) { "nativeAD { admob: not imported }" }
                }
            }
        }
    }

    init {
        vb.nativeInterstitialClose.setOnClickListener {
            vb.root.isVisible = false
            callback.onClosed(isCompleted = true)
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
            sspNativeAD?.loadAd() ?: run {
                logger.i(viewName = tagName) { "requestAD -> fail { loader: null, networkName: $networkName }" }
                callback.ondFailed(isBlocked = false)
            }
        }
    }

    override fun show(action: (success: Boolean) -> Unit) {
        if (sspNativeAD?.isLoaded == true && weakActivity.get() != null) {
            val parent: ViewGroup = weakActivity.get()!!.findViewById(android.R.id.content)
            parent.findViewById<RelativeLayout>(R.id.acb_support_ad_interstitial_root_view)?.let {
                parent.removeView(it)
            }
            vb.root.id = R.id.acb_support_ad_interstitial_root_view
            parent.addView(vb.root)
            action(true)
        } else {
            action(false)
        }
    }

    override fun onResume() {
        // nope
    }

    override fun onPause() {
        // nope
    }

    override fun release() {
        try {
            sspNativeAD?.setNativeAdEventCallbackListener(null)
            sspNativeAD?.removeAllViews()
            sspNativeAD?.destroy()
            sspNativeAD = null
            weakActivity.clear()
        } catch (e: Exception) {
            LogHandler.e(moduleName = MODULE_NAME) {
                "$tagName -> release { networkName: $networkName }"
            }
        }
    }

    override fun onNativeAdLoadSuccess() {
        sspNativeAD?.let {
            logger.i(viewName = tagName) { "onNativeAdLoadSuccess -> success { networkName: $networkName }" }
            callback.onLoaded()
        } ?: run {
            logger.i(viewName = tagName) { "onNativeAdLoadSuccess -> failed { nativeAD: null, networkName: $networkName }" }
            callback.ondFailed(isBlocked = false)
        }
    }

    override fun onNativeAdLoadFailed(sspErrorCode: SSPErrorCode?) {
        logger.i(viewName = tagName) { "onNativeAdLoadFailed { code: ${sspErrorCode?.errorCode}, message: ${sspErrorCode?.errorMessage}, networkName: $networkName }" }
        callback.ondFailed(isBlocked = Curator.isBlocked(sspErrorCode))
    }

    override fun onImpression() {
        logger.i(viewName = tagName) { "onImpression(onOpened) { networkName: $networkName }" }
        callback.onOpened()
    }

    override fun onClicked() {
        logger.i(viewName = tagName) { "onClicked { networkName: $networkName }" }
    }
}