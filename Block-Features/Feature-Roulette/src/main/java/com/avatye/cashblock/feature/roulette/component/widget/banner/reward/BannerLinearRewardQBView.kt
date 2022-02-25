package com.avatye.cashblock.feature.roulette.component.widget.banner.reward

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.avatye.cashblock.base.component.contract.api.RewardBannerApiContractor
import com.avatye.cashblock.base.component.contract.business.AccountContractor
import com.avatye.cashblock.base.component.contract.business.CoreContractor
import com.avatye.cashblock.base.component.contract.business.SettingContractor
import com.avatye.cashblock.base.component.domain.entity.banner.BannerRewardCampaignEntity
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.component.domain.entity.user.AgeVerifiedType
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.support.CoreUtil
import com.avatye.cashblock.feature.roulette.R
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.RouletteConfig.logger
import com.avatye.cashblock.feature.roulette.component.model.parcel.RewardBannerBrowserParcel
import com.avatye.cashblock.feature.roulette.component.widget.banner.IBannerLinearLifeCycle
import com.avatye.cashblock.feature.roulette.databinding.AcbsrWidgetBannerLinearRewardQbBinding
import com.avatye.cashblock.feature.roulette.presentation.view.miscellaneous.RewardBannerBrowserActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.lang.ref.WeakReference

internal class BannerLinearRewardQBView(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs), IBannerLinearLifeCycle {

    private val viewName: String = "BannerLinearRewardQBView"

    var ownerActivity: Activity? = null
    var rewardCallback: IBannerLinearRewardCallback? = null
    private var rewardVerification: Boolean = false


    private val leakHandler = LeakHandler()
    private val leakRunnable: Runnable = Runnable {
        rewardCallback?.onAdFail()
    }


    private val weakContext = WeakReference(context)
    private val vb: AcbsrWidgetBannerLinearRewardQbBinding by lazy {
        AcbsrWidgetBannerLinearRewardQbBinding.inflate(LayoutInflater.from(context), this, true)
    }


    private val allowAd: Boolean
        get() {
            return SettingContractor.rewardBannerSetting.roulette.quantumbit.allowAd
        }

    private val ageVerified: Boolean
        get() {
            return AccountContractor.ageVerified == AgeVerifiedType.VERIFIED
        }


    private val api: RewardBannerApiContractor by lazy {
        RewardBannerApiContractor(blockType = RouletteConfig.blockType, serviceType = ServiceType.ROULETTE)
    }


    override fun onResume() {
    }


    override fun onPause() {
    }


    override fun onDestroy() {
        leakHandler.removeCallbacks(leakRunnable)
    }


    fun requestAD() {
        if (!ageVerified || !allowAd) {
            this@BannerLinearRewardQBView.isVisible = false
            rewardCallback?.onAdFail()
            return
        }
        requestBannerCampaign()
    }


    private fun requestBannerCampaign() {
        val deviceAAID = CoreContractor.DeviceSetting.aaid
        if (deviceAAID.isEmpty()) {
            logger.i(viewName = viewName) { "requestBannerCampaign { deviceAAID: isEmpty }" }
            this@BannerLinearRewardQBView.isVisible = false
            rewardCallback?.onAdFail()
            return
        }
        //
        api.retrieveRewardCampaign(deviceAAID = deviceAAID) {
            when (it) {
                is ContractResult.Success -> viewBindAdContent(entity = it.contract)
                is ContractResult.Failure -> {
                    logger.i(viewName = viewName) { "requestBannerCampaign: $it" }
                    this@BannerLinearRewardQBView.isVisible = false
                    rewardCallback?.onAdFail()
                }
            }
        }
    }

    private fun viewBindAdContent(entity: BannerRewardCampaignEntity) {
        Glide.with(context).load(entity.imageUrl).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                logger.e(throwable = e, viewName = viewName) { "viewBindAdContent" }
                this@BannerLinearRewardQBView.isVisible = false
                rewardCallback?.onAdFail()
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                resource?.let {
                    setRewardAction(entity = entity)
                    rewardVerification = true
                    vb.rewardBannerImage.isVisible = true
                    vb.rewardBannerImage.setImageDrawable(resource)
                    rewardCallback?.onReward(rewardAmount = entity.reward)
                    leakHandler.postDelayed(leakRunnable, entity.limitSeconds * 1000)
                } ?: run {
                    logger.i(viewName = viewName) { "onResourceReady: resource is null" }
                    this@BannerLinearRewardQBView.isVisible = false
                    rewardCallback?.onAdFail()
                }
                return false
            }
        }).preload()
    }


    private fun setRewardAction(entity: BannerRewardCampaignEntity) {
        vb.rewardBannerImage.setOnClickListener {
            if (rewardVerification) {
                CoreContractor.DeviceSetting.retrieveAAID {
                    if (it.isValid) {
                        rewardVerification = false
                        requestRewardParticipate(deviceAAID = it.aaid, advertiseID = entity.advertiseID)
                    } else {
                        // refresh
                        leakHandler.removeCallbacks(leakRunnable)
                        requestAD()
                    }
                }
            }
        }
    }


    private fun requestRewardParticipate(deviceAAID: String, advertiseID: String) {
        api.postRewardParticipate(deviceAAID = deviceAAID, advertiseID = advertiseID) {
            when (it) {
                is ContractResult.Success -> {
                    ownerActivity?.let { activity ->
                        RewardBannerBrowserActivity.open(
                            activity = activity,
                            parcel = RewardBannerBrowserParcel(clickID = it.contract.clickID, landingUrl = it.contract.landingUrl)
                        )
                    } ?: run {
                        CoreUtil.showToast(R.string.acb_common_message_error)
                        this@BannerLinearRewardQBView.isVisible = false
                        rewardCallback?.onAdFail()
                    }
                }
                is ContractResult.Failure -> {
                    CoreUtil.showToast(message = it.message.ifEmpty { context.getString(R.string.acb_common_message_error) })
                    this@BannerLinearRewardQBView.isVisible = false
                    rewardCallback?.onAdFail()
                }
            }
        }
    }


    class LeakHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            // nope
        }
    }

}