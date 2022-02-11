package com.avatye.cashblock.base.component.widget.banner

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.avatye.cashblock.R
import com.avatye.cashblock.base.MODULE_NAME
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.base.library.ad.curator.IADAgeVerifier
import com.avatye.cashblock.base.library.ad.curator.linear.CuratorLinear
import com.avatye.cashblock.base.library.ad.curator.linear.ICuratorLinearCallback
import com.avatye.cashblock.base.library.ad.curator.linear.loader.LinearADSize
import com.avatye.cashblock.databinding.AcbLibraryAdWidgetBannerLinearBinding
import com.igaworks.ssp.part.banner.AdPopcornSSPBannerAd

class BannerLinearView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs), ICuratorLinearCallback {

    private val tagName: String = "BannerLinearView"

    enum class SourceType { ROULETTE, OFFERWALL }

    data class BannerData(
        val placementAppKey: String,
        val placementADSize: LinearADSize,
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
    var sourceType: SourceType? = null

    private var curatorLinear: CuratorLinear? = null
    private val vb: AcbLibraryAdWidgetBannerLinearBinding by lazy {
        AcbLibraryAdWidgetBannerLinearBinding.inflate(LayoutInflater.from(context), this, true)
    }

    private fun setBannerBackFill(isVisible: Boolean) {
        if (bannerData != null && sourceType != null) {
            val backFillResId = getBannerBackFillResourceId(sourceType = sourceType!!, linearADSize = bannerData!!.placementADSize)
            if (isVisible && backFillResId > 0) {
                when (bannerData!!.placementADSize) {
                    LinearADSize.W320XH50 -> {
                        // 320x50
                        vb.bannerLinearBackFill320x50.isVisible = true
                        vb.bannerLinearBackFill320x50.setImageResource(backFillResId)
                        // 320x100
                        vb.bannerLinearBackFill320x100.isVisible = false
                    }
                    LinearADSize.W320XH100 -> {
                        // 320x100
                        vb.bannerLinearBackFill320x100.isVisible = true
                        vb.bannerLinearBackFill320x100.setImageResource(backFillResId)
                        // 320x50
                        vb.bannerLinearBackFill320x50.isVisible = false
                    }
                }
                return
            }
        }
        vb.bannerLinearBackFill320x50.isVisible = false
        vb.bannerLinearBackFill320x100.isVisible = false
    }

    private fun getBannerBackFillResourceId(sourceType: SourceType, linearADSize: LinearADSize): Int {
        return if (linearADSize == LinearADSize.W320XH50) {
            when (sourceType) {
                SourceType.ROULETTE -> R.drawable.acb_common_bitmap_roulette_banner_linear_back_fill_h50
                SourceType.OFFERWALL -> 0
            }
        } else {
            when (sourceType) {
                SourceType.ROULETTE -> R.drawable.acb_common_bitmap_roulette_banner_linear_back_fill_h100
                SourceType.OFFERWALL -> 0
            }
        }
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
                placementADSize = it.placementADSize,
                sspPlacementID = it.sspPlacementID,
                nativePlacementID = it.nativePlacementID,
                mediationExtraData = it.makeMediationExtra(),
                verifier = it.verifier,
                callback = this@BannerLinearView
            )
            this@BannerLinearView.isVisible = true
            setBannerBackFill(isVisible = true)
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
            setBannerBackFill(isVisible = false)
        }
    }

    override fun onFailure(isBlocked: Boolean) {
        this.post {
            LogHandler.i(moduleName = MODULE_NAME) { "$tagName -> curatorLinear::onFailure(isBlock: $isBlocked)" }
            vb.bannerLinearFrame.isVisible = false
            setBannerBackFill(isVisible = true)
        }
    }

    override fun onNeedAgeVerification() {
        this@BannerLinearView.isVisible = false
    }
}