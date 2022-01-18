package com.avatye.cashblock.base.library.ad.curator.queue.loader.box

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.avatye.cashblock.R
import com.avatye.cashblock.base.Core.logger
import com.avatye.cashblock.base.library.ad.curator.ADNetworkType
import com.avatye.cashblock.base.library.ad.curator.Curator
import com.avatye.cashblock.base.library.ad.curator.popup.loader.PopupADLoader
import com.avatye.cashblock.base.library.ad.curator.queue.loader.ADLoaderBase
import com.avatye.cashblock.base.library.ad.curator.queue.loader.ADLoaderType
import com.avatye.cashblock.base.library.ad.curator.queue.loader.IADLoaderCallback
import com.avatye.cashblock.databinding.AcbLibraryAdLayoutBoxBannerBinding
import com.igaworks.ssp.BannerAnimType
import com.igaworks.ssp.SSPErrorCode
import com.igaworks.ssp.part.banner.AdPopcornSSPBannerAd
import com.igaworks.ssp.part.banner.listener.IBannerEventCallbackListener
import java.lang.ref.WeakReference

internal class BoxBannerADLoader(
    private val activity: Activity,
    private val placementAppKey: String,
    private val placementID: String,
    private val callback: IADLoaderCallback
) : ADLoaderBase(), IBannerEventCallbackListener {

    companion object {
        const val tagName: String = "BoxBannerADLoader"
    }

    override val loaderType = ADLoaderType.BOX_BANNER
    private val weakContext = WeakReference(activity)
    private val networkName: String get() = ADNetworkType.from(sspBannerAD?.currentNetwork ?: 0).name

    private val vb: AcbLibraryAdLayoutBoxBannerBinding by lazy {
        AcbLibraryAdLayoutBoxBannerBinding.inflate(LayoutInflater.from(weakContext.get()))
    }

    private var sspBannerAD: AdPopcornSSPBannerAd? = null
    private fun createAdvertiseInstance() {
        if (sspBannerAD == null) {
            sspBannerAD = AdPopcornSSPBannerAd(weakContext.get()?.application).apply {
                this.autoBgColor = false
                this.placementId = placementID
                this.setPlacementAppKey(placementAppKey)
                this.setAdSize(com.igaworks.ssp.AdSize.BANNER_300x250)
                this.setBannerAnimType(BannerAnimType.NONE)
                this.setRefreshTime(Curator.RefreshTime)
                this.setNetworkScheduleTimeout(Curator.NetworkScheduleTimeout)
                this.setBannerEventCallbackListener(this@BoxBannerADLoader)
            }
        }
    }

    init {
        vb.boxBannerClose.setOnClickListener {
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
            sspBannerAD?.loadAd() ?: run {
                logger.i(viewName = PopupADLoader.tagName) { "requestAD -> fail { loader: null, networkName: $networkName }" }
                callback.ondFailed(isBlocked = false)
            }
        }
    }

    override fun show(action: (success: Boolean) -> Unit) {
        weakContext.get()?.let {
            val parent: ViewGroup = it.findViewById(android.R.id.content)
            parent.findViewById<FrameLayout>(R.id.acb_support_ad_box_banner_root_view)?.let { v ->
                parent.removeView(v)
            }
            vb.root.id = R.id.acb_support_ad_box_banner_root_view
            parent.addView(vb.root)
            parent.post { callback.onOpened() }
            action(true)
        } ?: run {
            action(false)
        }
    }

    override fun onResume() {
        sspBannerAD?.onResume()
    }

    override fun onPause() {
        sspBannerAD?.onPause()
    }

    override fun release() {
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
            logger.i(viewName = PopupADLoader.tagName) { "OnBannerAdReceiveSuccess -> success { networkName: $networkName} }" }
            vb.boxBannerContent.addView(it)
            callback.onLoaded()
        } ?: run {
            logger.i(viewName = PopupADLoader.tagName) { "OnBannerAdReceiveSuccess -> fail { loader: null, networkName: $networkName} }" }
            callback.ondFailed(isBlocked = false)
        }
    }

    override fun OnBannerAdReceiveFailed(sspErrorCode: SSPErrorCode?) {
        logger.i(viewName = PopupADLoader.tagName) { "OnBannerAdReceiveFailed { code: ${sspErrorCode?.errorCode}, message: ${sspErrorCode?.errorMessage}, networkName: $networkName }" }
        callback.ondFailed(isBlocked = Curator.isBlocked(sspErrorCode))
    }

    override fun OnBannerAdClicked() {
        logger.i(viewName = PopupADLoader.tagName) { "OnBannerAdClicked { networkName: $networkName} }" }
    }
    // endregion
}