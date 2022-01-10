package com.avatye.cashblock.base.component.widget.banner

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.avatye.cashblock.base.MODULE_NAME
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.base.library.ad.curator.IADAgeVerifier
import com.avatye.cashblock.base.library.ad.curator.linear.CuratorLinear
import com.avatye.cashblock.base.library.ad.curator.linear.ICuratorLinearCallback
import com.avatye.cashblock.databinding.AcbLibraryAdWidgetBannerLinearBinding
import com.igaworks.ssp.part.banner.AdPopcornSSPBannerAd

class BannerLinearView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs), ICuratorLinearCallback {

    private val tagName: String = "BannerLinearView"

    data class BannerData(
        val placementAppKey: String,
        val sspPlacementID: String,
        val nativePlacementID: String,
        val mezzo: MediationMezzoData?,
        val verifier: IADAgeVerifier
    ) {
        fun makeMediationExtra(): HashMap<String, Any>? {
            return when (mezzo) {
                null -> null
                else -> hashMapOf(
                    AdPopcornSSPBannerAd.MediationExtraData.MEZZO_STORE_URL to mezzo.storeUrl,
                    AdPopcornSSPBannerAd.MediationExtraData.MEZZO_IS_USED_BACKGROUND_CHECK to mezzo.allowBackground,
                    AdPopcornSSPBannerAd.MediationExtraData.MEZZO_AGE_LEVEL to if (verifier.isVerified()) 1 else 0
                )
            }
        }
    }

    data class MediationMezzoData(val storeUrl: String, val allowBackground: Boolean = false)

    var bannerData: BannerData? = null
    private var curatorLinear: CuratorLinear? = null
    private val vb: AcbLibraryAdWidgetBannerLinearBinding by lazy {
        AcbLibraryAdWidgetBannerLinearBinding.inflate(LayoutInflater.from(context), this, true)
    }

    private fun requestCuratorLinear() {
        bannerData?.let {
            // allow-age-verified
            if (!it.verifier.isVerified()) {
                this@BannerLinearView.isVisible = false
                return
            }
            // check curator
            if (curatorLinear != null) {
                return
            }
            // make-a-curator
            curatorLinear = CuratorLinear(
                context = this.context,
                placementAppKey = it.placementAppKey,
                sspPlacementID = it.sspPlacementID,
                nativePlacementID = it.nativePlacementID,
                mediationExtraData = it.makeMediationExtra(),
                verifier = it.verifier,
                callback = this@BannerLinearView
            )
            this@BannerLinearView.isVisible = true
            post {
                curatorLinear?.requestAD()
            }
        } ?: run {
            this@BannerLinearView.isVisible = false
        }
    }

    fun requestBanner() = requestCuratorLinear()

    fun refresh() {
        if (curatorLinear == null) {
            requestCuratorLinear()
        }
    }

    fun onResume() {
        // logging
        LogHandler.i(moduleName = MODULE_NAME) { "$tagName -> onResume" }
        // check-curator
        if (curatorLinear == null) {
            requestCuratorLinear()
            return
        }
        // resume-curator
        curatorLinear?.onResume()
    }

    fun onPause() {
        // logging
        LogHandler.i(moduleName = MODULE_NAME) { "$tagName -> onPause" }
        // pause-curator
        curatorLinear?.onPause()
    }

    fun onDestroy() {
        try {
            curatorLinear?.release()
            curatorLinear = null
        } catch (e: Exception) {
            LogHandler.e(throwable = e, moduleName = MODULE_NAME) { "$tagName -> onDestroy" }
        }
    }

    override fun oSuccess(adView: View) {
        this.post {
            LogHandler.i(moduleName = MODULE_NAME) { "$tagName -> curatorLinear::onSuccess" }
            vb.bannerLinearFrame.removeAllViews()
            vb.bannerLinearFrame.addView(adView)
            vb.bannerLinearFrame.isVisible = true
            vb.bannerLinearBackfill.isVisible = false
        }
    }

    override fun onFailure(isBlocked: Boolean) {
        this.post {
            LogHandler.i(moduleName = MODULE_NAME) { "$tagName -> curatorLinear::onFailure(isBlock: $isBlocked)" }
            vb.bannerLinearFrame.isVisible = false
            vb.bannerLinearBackfill.isVisible = true
        }
    }

    override fun onNeedAgeVerification() {
        this@BannerLinearView.isVisible = false
    }
}