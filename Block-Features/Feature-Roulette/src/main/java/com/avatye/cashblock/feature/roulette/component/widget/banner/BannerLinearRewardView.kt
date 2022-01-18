package com.avatye.cashblock.feature.roulette.component.widget.banner

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.avatye.cashblock.base.component.contract.api.RewardBannerApiContractor
import com.avatye.cashblock.base.component.contract.business.AccountContractor
import com.avatye.cashblock.base.component.contract.business.SettingContractor
import com.avatye.cashblock.base.component.domain.entity.user.AgeVerifiedType
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.support.CoreUtil
import com.avatye.cashblock.base.component.support.hostPackageName
import com.avatye.cashblock.feature.roulette.R
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.RouletteConfig.logger
import com.avatye.cashblock.feature.roulette.component.data.PreferenceData
import com.avatye.cashblock.feature.roulette.component.livedata.TicketBalanceLiveData
import com.avatye.cashblock.feature.roulette.databinding.AcbsrWidgetBannerLinearRewardBinding
import com.mmc.man.AdConfig
import com.mmc.man.AdEvent
import com.mmc.man.AdListener
import com.mmc.man.AdResponseCode
import com.mmc.man.data.AdData
import com.mmc.man.view.AdManView
import org.joda.time.DateTime
import java.lang.ref.WeakReference

internal class BannerLinearRewardView(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs), AdListener {

    internal interface RewardCallback {
        fun onReward(hasReward: Boolean)
        fun onAdFail()
    }

    private val tagName: String = "BannerLinearRewardView"

    private val leakHandler = LeakHandler()

    private class LeakHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            // nope
        }
    }

    private val binding: AcbsrWidgetBannerLinearRewardBinding by lazy {
        AcbsrWidgetBannerLinearRewardBinding.inflate(LayoutInflater.from(context), this, true)
    }

    var rewardCallback: RewardCallback? = null

    var hasReward: Boolean = false
        set(value) {
            field = value
            rewardCallback?.onReward(field)
        }

    private val api: RewardBannerApiContractor by lazy {
        RewardBannerApiContractor(blockType = RouletteConfig.blockType)
    }
    private val hostPackageName = context.hostPackageName
    private val hostAppName = SettingContractor.appInfoSetting.appName
    private val hostStoreUrl = SettingContractor.appInfoSetting.storeUrl

    private var lastClickTime: Long = 0
    private val frequency: Long = (10 * 60 * 1000)
    private val messageDelay: Long get() = SettingContractor.inAppSetting.main.rewardBannerDelay

    private val weakContext = WeakReference(context)
    private var bannerView: AdManView? = null
    private val publisherCode: Int by lazy { SettingContractor.advertiseNetworkSetting.manPlus.publisherCode }
    private val mediaCode: Int by lazy { SettingContractor.advertiseNetworkSetting.manPlus.mediaCode }
    private val sectionCode = try {
        SettingContractor.inAppSetting.main.pid.rewardBanner.toInt()
    } catch (e: Exception) {
        logger.e(viewName = tagName) { "sectionCode parsing error" }
        0
    }
    private val ageVerified: Boolean
        get() {
            return AccountContractor.ageVerified == AgeVerifiedType.VERIFIED
        }

    init {
        requestLinearRewardView()
    }

    private fun requestLinearRewardView() {
        if (!ageVerified) {
            this@BannerLinearRewardView.isVisible = false
            return
        }

        if (bannerView == null) {
            logger.i(viewName = tagName) { "bannerView -> create" }
            bannerView = AdManView(weakContext.get())
            AdManView.init(context, leakHandler)
            this@BannerLinearRewardView.isVisible = false
        }

        // make instance
        val adData = AdData()
        adData.major(
            "main_view_banner",
            AdConfig.TYPE_HTML,
            publisherCode,
            mediaCode,
            sectionCode,
            hostStoreUrl,
            hostPackageName,
            hostAppName,
            320,
            50
        )
        adData.userAgeLevel = if (ageVerified) 1 else 0
        adData.isPermission(AdConfig.NOT_USED, AdConfig.NOT_USED)
        adData.setApiModule(AdConfig.NOT_USED, AdConfig.NOT_USED)
        bannerView?.setData(adData, this)
        bannerView?.addBannerView(binding.linearRewardBannerFrame)
        binding.linearRewardBannerFrame.post {
            bannerView?.request(leakHandler)
        }
    }

    private fun destroyLinearRewardView() {
        runCatching {
            bannerView?.onDestroy()
        }.onFailure {
            logger.e(viewName = tagName, throwable = it) { "destroyLinearRewardView" }
        }
    }

    fun onResume() {
        logger.i(viewName = tagName) { "onResume -> requestLinearRewardView" }
        requestLinearRewardView()
    }

    fun onPause() {
        logger.i(viewName = tagName) { "onPause -> finishLinearRewardBanner" }
        destroyLinearRewardView()
    }

    fun onDestroy() {
        try {
            bannerView?.removeAllViews()
            bannerView = null
            weakContext.clear()
            logger.i(viewName = tagName) { "onDestroy" }
        } catch (e: Exception) {
            logger.e(viewName = tagName, throwable = e) { "onDestroy" }
        }
    }

    override fun onAdSuccessCode(v: Any?, id: String?, type: String?, status: String?, jsonDataString: String?) {
        if (!ageVerified) {
            return
        }
        (context as Activity).runOnUiThread {
            logger.i(viewName = tagName) { "onAdSuccessCode -> AdResponseCode { status: $status, type: $type }" }
            if (status == AdResponseCode.Status.SUCCESS) {
                this@BannerLinearRewardView.visibility = View.VISIBLE
                bannerView?.addBannerView(binding.linearRewardBannerFrame)
                // reward
                if (type == AdResponseCode.Type.GUARANTEE) {
                    //광고 성공 : 유료
                    val frequencyTime = DateTime().millis - PreferenceData.BannerReward.frequency
                    when (frequencyTime >= frequency) {
                        true -> requestReward()
                        false -> {
                            hasReward = PreferenceData.BannerReward.rewardable
                        }
                    }
                } else {
                    //광고 성공 : 무료
                    hasReward = false
                }
            }
        }
    }

    override fun onAdFailCode(v: Any?, id: String?, type: String?, status: String?, jsonString: String?) {
        if (!ageVerified) {
            return
        }
        post {
            logger.e(viewName = tagName) { "onAdFailCode($status)" }
            this@BannerLinearRewardView.visibility = View.GONE
            destroyLinearRewardView()
            rewardCallback?.onAdFail()
        }
    }

    override fun onAdErrorCode(v: Any?, id: String?, type: String?, status: String?, failingUrl: String?) {
        if (!ageVerified) {
            return
        }
        post {
            logger.e(viewName = tagName) { "onAdErrorCode($status)" }
            this@BannerLinearRewardView.visibility = View.GONE
            destroyLinearRewardView()
            rewardCallback?.onAdFail()
        }
    }

    override fun onAdEvent(v: Any?, id: String?, type: String?, status: String?, jsonDataString: String?) {
        if (!ageVerified) {
            return
        }
        if (type == AdEvent.Type.CLICK) {
            if (SystemClock.elapsedRealtime() - lastClickTime < messageDelay) {
                return
            }
            val rewardAble = PreferenceData.BannerReward.rewardable
            val ticketTransactionID = PreferenceData.BannerReward.transactionId
            if (rewardAble && ticketTransactionID.isNotEmpty()) {
                hasReward = false
                CoreUtil.showToast(R.string.acbsr_string_linear_reward_banner_click_message)
                leakHandler.postDelayed({ postReward(ticketTransactionID) }, messageDelay)
            }
            lastClickTime = SystemClock.elapsedRealtime()
            return
        }
    }

    override fun onPermissionSetting(v: Any?, id: String?) {
        return
    }

    private fun requestReward() {
        if (!ageVerified) {
            return
        }
        api.retrieveDirectReward {
            when (it) {
                is ContractResult.Success -> {
                    PreferenceData.BannerReward.update(
                        frequency = DateTime().millis,
                        rewardable = it.contract.rewardable,
                        amount = it.contract.reward,
                        transactionId = it.contract.transactionId
                    )
                    hasReward = it.contract.rewardable
                }
                is ContractResult.Failure -> {
                    PreferenceData.BannerReward.update(rewardable = false, amount = 0, transactionId = "")
                    hasReward = false
                }
            }
        }
    }

    private fun postReward(transactionID: String) {
        if (!ageVerified) {
            return
        }
        try {
            api.postDirectReward(transactionId = transactionID) {
                when (it) {
                    is ContractResult.Success -> {
                        logger.i(viewName = tagName) { "postReward -> ${it.contract}" }
                        PreferenceData.BannerReward.update(frequency = DateTime().millis, rewardable = false, amount = 0, transactionId = "")
                        CoreUtil.showToast(R.string.acbsr_string_linear_reward_banner_complete_message)
                        if (it.contract.balance > 0) {
                            TicketBalanceLiveData.synchronization(it.contract.balance)
                        }
                        hasReward = false
                    }
                    is ContractResult.Failure -> {
                        logger.i(viewName = tagName) { "postReward -> error: $it" }
                        PreferenceData.BannerReward.update(frequency = DateTime().millis, rewardable = false, amount = 0, transactionId = "")
                        hasReward = false
                        CoreUtil.showToast(it.message)
                    }
                }
            }
        } catch (e: Exception) {
            logger.e(viewName = tagName, throwable = e) { "postReward -> postGiveBannerReward" }
        }
    }
}