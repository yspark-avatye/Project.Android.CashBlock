package com.avatye.cashblock.feature.roulette.presentation.view.miscellaneous

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.OvershootInterpolator
import android.webkit.*
import androidx.core.view.isVisible
import com.avatye.cashblock.base.component.contract.api.RewardBannerApiContractor
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.support.*
import com.avatye.cashblock.base.component.widget.miscellaneous.ScrollWebView
import com.avatye.cashblock.feature.roulette.R
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.RouletteConfig.logger
import com.avatye.cashblock.feature.roulette.component.livedata.TicketBalanceLiveData
import com.avatye.cashblock.feature.roulette.component.model.parcel.RewardBannerBrowserParcel
import com.avatye.cashblock.feature.roulette.databinding.AcbsrActivityRewardBannerBroserBinding
import com.avatye.cashblock.feature.roulette.presentation.AppBaseActivity
import java.net.URISyntaxException

internal class RewardBannerBrowserActivity : AppBaseActivity() {

    internal companion object {
        fun open(activity: Activity, parcel: RewardBannerBrowserParcel, close: Boolean = false) {
            activity.launch(
                intent = Intent(activity, RewardBannerBrowserActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra(RewardBannerBrowserParcel.NAME, parcel)
                },
                transition = ActivityTransitionType.NONE.value,
                close = close
            )
        }
    }


    private val vb: AcbsrActivityRewardBannerBroserBinding by lazy {
        AcbsrActivityRewardBannerBroserBinding.inflate(LayoutInflater.from(this))
    }
    private val api: RewardBannerApiContractor by lazy {
        RewardBannerApiContractor(blockType = RouletteConfig.blockType, serviceType = ServiceType.ROULETTE)
    }


    private val millisInFuture: Long = 7 * 1000
    private val countDownInterval: Long = 100L
    private var requestLandingPage = false
    private var isRewardRequested = false
    private var isTimeTicking = false
    private var countDownTimer: CountDownTimer? = null
    private lateinit var parcel: RewardBannerBrowserParcel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        // webview
        vb.webContent.webViewClient = CustomWebViewClient()
        vb.webContent.webChromeClient = CustomWebChromeClient()
        vb.webContent.scrollCallback = callbackScroll
        // parcel
        extraParcel<RewardBannerBrowserParcel>(RewardBannerBrowserParcel.NAME)?.let {
            parcel = it
            isRewardRequested = false
            loadingView?.show(cancelable = false)
            vb.webContent.loadUrl(parcel.landingUrl)
        } ?: run {
            CoreUtil.showToast(message = R.string.acb_common_message_error)
            leakHandler.postDelayed({ finish() }, 750L)
        }
    }


    private fun animateProgress() {
        showBubbleTips()
        vb.rewardCircleProgress.progressMin = 0F
        vb.rewardCircleProgress.progressMax = millisInFuture.toFloat()
        countDownTimer = object : CountDownTimer(millisInFuture, countDownInterval) {
            override fun onFinish() {
                try {
                    vb.rewardTimeText.text = "0"
                    vb.rewardProgressAnimationGroup.animateFadeOut(callback = object : AnimationEventCallback() {
                        override fun onAnimationEnd(animation: Animation?) {
                            vb.rewardProgressAnimationGroup.isVisible = false
                            vb.pageClose.animateFadeIn()
                            vb.rewardTimeBubbleTips.animateFadeOut(callback = object : AnimationEventCallback() {
                                override fun onAnimationEnd(animation: Animation?) {
                                    vb.rewardTimeBubbleTips.isVisible = false
                                    super.onAnimationEnd(animation)
                                }
                            })
                            vb.pageClose.setOnClickListener { finish() }
                        }
                    })
                    if (!isRewardRequested) {
                        requestReward()
                    }
                } catch (e: Exception) {
                    logger.e(throwable = e, viewName = viewTag) { "animateProgress" }
                }
            }

            override fun onTick(millisUntilFinished: Long) {
                try {
                    val progressValue = millisInFuture - millisUntilFinished
                    val countText = (millisUntilFinished / 1000).toInt() + 1
                    vb.rewardCircleProgress.updateSmoothProgressValue(progressValue.toFloat())
                    vb.rewardTimeText.text = "$countText"
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        countDownTimer?.start()
    }


    private fun requestReward() {
        isRewardRequested = true
        api.putRewardComplete(clickID = parcel.clickID) {
            when (it) {
                is ContractResult.Success -> {
                    CoreUtil.showToast(message = R.string.acbsr_string_linear_reward_banner_complete_message)
                    TicketBalanceLiveData.synchronization { success, syncValue ->
                        logger.i(viewName = viewTag) { "requestReward -> complete { success: $success, syncValue:$syncValue }" }
                    }
                }
                is ContractResult.Failure -> {
                    CoreUtil.showToast(message = it.message.ifEmpty { getString(R.string.acb_common_message_error) })
                }
            }
        }
    }


    private fun showBubbleTips() {
        vb.rewardTimeBubbleTips.alpha = 0f
        vb.rewardTimeBubbleTips.isVisible = true
        AnimatorSet().apply {
            duration = 250
            startDelay = 250
            interpolator = OvershootInterpolator()
            playTogether(
                ObjectAnimator.ofFloat(vb.rewardTimeBubbleTips, "alpha", 0F, 1F),
                ObjectAnimator.ofFloat(vb.rewardTimeBubbleTips, "scaleX", 0.8F, 1.2F, 1F),
                ObjectAnimator.ofFloat(vb.rewardTimeBubbleTips, "scaleY", 0.8F, 1.2F, 1F)

            )
        }.start()
    }


    private inner class CustomWebViewClient : WebViewClient() {
        override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
            val requestUrl = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                request?.url.toString()
            } else {
                view?.url.toString()
            }
            logger.i(viewName = viewTag) { "CustomWebViewClient -> shouldOverrideUrlLoading : shouldInterceptRequest -> $requestUrl" }
            if (!requestLandingPage) {
                requestLandingPage = (requestUrl == parcel.landingUrl)
            }
            return super.shouldInterceptRequest(view, request)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            logger.i(viewName = viewTag) { "CustomWebViewClient -> onPageFinished: $url" }
            loadingView?.dismiss()
            if (requestLandingPage) {
                if (!isTimeTicking) {
                    isTimeTicking = true
                    vb.rewardProgressAnimationGroup.animateScaleIn {
                        animateProgress()
                    }
                }
            } else {
                CoreUtil.showToast(message = R.string.acb_common_message_error)
                leakHandler.postDelayed({ finish() }, 250L)
            }
            super.onPageFinished(view, url)
        }

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            val requestUrl = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                request?.url.toString()
            } else {
                view?.url.toString()
            }
            // check url
            if (requestUrl.isEmpty() || view == null) {
                return false
            }

            if (requestUrl.startsWith("https") || requestUrl.startsWith("http")) {
                return super.shouldOverrideUrlLoading(view, request)
            } else if (requestUrl.startsWith("intent://")) {
                try {
                    val intent = Intent.parseUri(requestUrl, Intent.URI_INTENT_SCHEME)
                    val existPackage = packageManager.getLaunchIntentForPackage(intent.getPackage()!!)
                    if (existPackage != null) {
                        startActivity(intent)
                    } else {
                        val marketIntent = Intent(Intent.ACTION_VIEW)
                        marketIntent.data = Uri.parse("market://details?id=" + intent.getPackage())
                        startActivity(marketIntent)
                    }
                } catch (e: Exception) {
                    logger.e(throwable = e, viewName = viewTag) { "shouldOverrideUrlLoading -> intent" }
                }
                return true
            } else if (requestUrl.startsWith("market://")) {
                try {
                    val intent = Intent.parseUri(requestUrl, Intent.URI_INTENT_SCHEME)
                    intent?.let { startActivity(it) }
                } catch (e: URISyntaxException) {
                    logger.e(throwable = e, viewName = viewTag) { "shouldOverrideUrlLoading -> market" }
                }
                return true
            } else {
                try {
                    val intent = Intent.parseUri(requestUrl, Intent.URI_INTENT_SCHEME)
                    val existPackage = packageManager.getLaunchIntentForPackage(intent.getPackage()!!)
                    if (existPackage != null) {
                        startActivity(intent)
                    } else {
                        val marketIntent = Intent(Intent.ACTION_VIEW)
                        marketIntent.data = Uri.parse("market://details?id=" + intent.getPackage())
                        startActivity(marketIntent)
                    }
                } catch (e: java.lang.Exception) {
                    logger.e(throwable = e, viewName = viewTag) { "shouldOverrideUrlLoading" }
                }
                return true
            }
        }
    }

    inner class CustomWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
        }
    }

    private val callbackScroll = object : ScrollWebView.IScrollComputeCallback {
        override fun onScrollCompute(webView: WebView?, offSetY: Int, scrollRange: Int, scrollExtent: Int) {
            webView?.let {
                val progressMaxValue = scrollRange - scrollExtent
                vb.webContentProgress.max = progressMaxValue
                vb.webContentProgress.progress = offSetY
            }
        }
    }


    override fun finish() {
        try {
            isRewardRequested = false
            vb.webContent.loadUrl("about:blank")
            vb.webContent.clearCache(true)
            vb.webContent.removeAllViews()
            vb.webContent.destroy()
        } catch (e: Exception) {
            logger.e(throwable = e, viewName = viewTag) { "finish" }
        }
        try {
            countDownTimer?.cancel()
            countDownTimer = null
        } catch (e: Exception) {
            logger.e(throwable = e, viewName = viewTag) { "finish" }
        }
        super.finish()
    }


    override fun onBackPressed() {
        if (vb.webContent.canGoBack()) {
            vb.webContent.goBack()
            return
        }
        return super.onBackPressed()
    }

}