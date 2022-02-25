package com.avatye.cashblock.feature.roulette.component.widget.banner.reward

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.avatye.cashblock.feature.roulette.component.widget.banner.IBannerLinearLifeCycle
import com.avatye.cashblock.feature.roulette.databinding.AcbsrWidgetBannerLinearRewardMedationBinding

internal class BannerLinearRewardMediationView(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs), IBannerLinearLifeCycle {

    private val viewName: String = "BannerLinearRewardMediationView"

    private val vb: AcbsrWidgetBannerLinearRewardMedationBinding by lazy {
        AcbsrWidgetBannerLinearRewardMedationBinding.inflate(LayoutInflater.from(context), this, true)
    }

    var ownerActivity: Activity? = null
        set(value) {
            if (field != value) {
                field = value
                vb.rewardBannerQb.ownerActivity = field
                vb.rewardBannerAce.ownerActivity = field
            }
        }
    var rewardCallback: IBannerLinearRewardCallback? = null
    private var rewardVerification: Boolean = false

    private val leakHandler = LeakHandler()

    private class LeakHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            // nope
        }
    }


    init {
        vb.rewardBannerAce.rewardCallback = object : IBannerLinearRewardCallback {
            override fun onReward(rewardAmount: Int) {
                vb.rewardBannerAce.isVisible = true
                vb.rewardBannerQb.isVisible = false
                rewardCallback?.onReward(rewardAmount = rewardAmount)
            }

            override fun onAdFail() {
                vb.rewardBannerAce.isVisible = false
                vb.rewardBannerQb.isVisible = false
                vb.rewardBannerQb.requestAD()
            }
        }
        vb.rewardBannerQb.rewardCallback = object : IBannerLinearRewardCallback {
            override fun onReward(rewardAmount: Int) {
                vb.rewardBannerAce.isVisible = false
                vb.rewardBannerQb.isVisible = true
                rewardCallback?.onReward(rewardAmount = rewardAmount)
            }

            override fun onAdFail() {
                vb.rewardBannerAce.isVisible = false
                vb.rewardBannerQb.isVisible = false
                rewardCallback?.onAdFail()
            }
        }
    }


    override fun onResume() {
        vb.rewardBannerAce.onResume()
        vb.rewardBannerQb.onResume()
    }

    override fun onPause() {
        vb.rewardBannerAce.onPause()
        vb.rewardBannerQb.onPause()
    }

    override fun onDestroy() {
        vb.rewardBannerAce.onDestroy()
        vb.rewardBannerQb.onDestroy()
    }

}