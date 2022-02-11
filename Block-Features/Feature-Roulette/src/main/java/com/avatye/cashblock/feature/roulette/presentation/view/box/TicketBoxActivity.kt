package com.avatye.cashblock.feature.roulette.presentation.view.box

import android.app.Activity
import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.view.isVisible
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.business.EventContractor
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.domain.entity.box.BoxAvailableEntity
import com.avatye.cashblock.base.component.domain.entity.box.BoxUseEntity
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult
import com.avatye.cashblock.base.component.support.*
import com.avatye.cashblock.base.component.widget.banner.BannerLinearView
import com.avatye.cashblock.base.component.widget.dialog.DialogPopupAgeVerifyView
import com.avatye.cashblock.base.library.ad.curator.ADNetworkType
import com.avatye.cashblock.base.library.ad.curator.popup.CuratorPopup
import com.avatye.cashblock.base.library.ad.curator.popup.ICuratorPopupCallback
import com.avatye.cashblock.base.library.ad.curator.queue.CuratorQueue
import com.avatye.cashblock.base.library.ad.curator.queue.ICuratorQueueCallback
import com.avatye.cashblock.base.library.ad.curator.queue.loader.ADLoaderBase
import com.avatye.cashblock.feature.roulette.R
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.RouletteConfig.logger
import com.avatye.cashblock.feature.roulette.component.controller.ADController
import com.avatye.cashblock.feature.roulette.component.controller.TicketBoxController
import com.avatye.cashblock.feature.roulette.component.livedata.TicketBalanceLiveData
import com.avatye.cashblock.feature.roulette.component.model.entity.ADPlacementType
import com.avatye.cashblock.feature.roulette.component.model.entity.ADQueueType
import com.avatye.cashblock.feature.roulette.component.model.entity.BannerLinearPlacementType
import com.avatye.cashblock.feature.roulette.databinding.AcbsrActivityTicketBoxBinding
import com.avatye.cashblock.feature.roulette.presentation.AppBaseActivity
import com.avatye.cashblock.feature.roulette.presentation.viewmodel.box.TicketBoxViewModel
import org.joda.time.DateTime

internal class TicketBoxActivity : AppBaseActivity() {

    internal companion object {
        fun open(activity: Activity, close: Boolean = false) {
            activity.launch(
                intent = Intent(activity, TicketBoxActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                },
                transition = ActivityTransitionType.NONE.value,
                close = close
            )
        }
    }

    private enum class TicketBoxStatus { LOADING, ACQUIRES, COMPLETE }

    private var isExcludeADNetwork = false
    private var isBoxOpened = false
    private var isPopupExposed: Boolean = false
    private val piecesCount = TicketBoxController.piecesCount
    private val popupExposeCount: Int by lazy { TicketBoxController.popupExposeCount }
    private var popupExposeTime = 0L

    private var currentCollectCount = 0
        set(value) {
            if (value <= TicketBoxController.piecesCount) {
                field = value
            }
        }

    private var advertisingLoadAnimation: AnimationDrawable? = null
    private var advertisingLoadAnimationPlay: Boolean
        get() {
            return advertisingLoadAnimation?.isRunning ?: false
        }
        set(value) {
            advertisingLoadAnimation?.let {
                if (value && !it.isRunning) {
                    it.start()
                    return
                }

                if (!value && it.isRunning) {
                    it.stop()
                    return
                }
            }
        }

    private var currentStatus = TicketBoxStatus.LOADING
        set(value) {
            if (value != field) {
                field = value
                when (field) {
                    TicketBoxStatus.LOADING -> {
                        vb.ticketBoxLoadingContainer.isVisible = true
                        vb.ticketBoxAcquireContainer.isVisible = false
                        vb.ticketBoxCompleteContainer.isVisible = false
                    }
                    TicketBoxStatus.ACQUIRES -> {
                        vb.ticketBoxLoadingContainer.isVisible = false
                        vb.ticketBoxAcquireContainer.isVisible = true
                        vb.ticketBoxCompleteContainer.isVisible = false
                        vb.ticketBoxAcquireDescription.text = getString(R.string.acbsr_string_ticket_box_acquire_description).toHtml
                        vb.ticketBoxAcquirePieces.setImageResource(TicketBoxController.pieces(0))
                        vb.ticketBoxAcquirePieces.animateScaleIn(
                            interpolator = OvershootInterpolator(3.5f),
                            duration = 350L,
                            callback = {
                                isPopupExposed = false
                                currentCollectCount = 0
                            }
                        )
                    }
                    TicketBoxStatus.COMPLETE -> {
                        vb.ticketBoxLoadingContainer.isVisible = false
                        vb.ticketBoxAcquireContainer.isVisible = false
                        vb.ticketBoxCompleteContainer.isVisible = true
                        vb.ticketBoxAppOpenButton.isVisible = false
                        vb.ticketBoxCompleteText.isVisible = false
                        vb.ticketBoxCompleteDescription.isVisible = false
                        TicketBoxController.animateComplete(vb.ticketBoxCompleteImage) {
                            vb.ticketBoxAppOpenButton.isVisible = true
                            vb.ticketBoxCompleteText.isVisible = true
                            vb.ticketBoxCompleteDescription.isVisible = true
                        }
                    }
                }
            }
        }

    // binding
    private val vb: AcbsrActivityTicketBoxBinding by lazy {
        AcbsrActivityTicketBoxBinding.inflate(LayoutInflater.from(this))
    }
    private val ticketBoxViewModel: TicketBoxViewModel by lazy {
        TicketBoxViewModel.create(viewModelStoreOwner = this)
    }

    // advertise curator
    private var openADCurator: CuratorQueue? = null
    private var closeADCurator: CuratorQueue? = null
    private var popupADCurator: CuratorPopup? = null


    override fun onBackPressed() {
        return
    }

    override fun onResume() {
        super.onResume()
        popupADCurator?.onResume()
        vb.bannerLinearView.onResume()
        vb.ticketBoxAdContainer.isVisible = false
    }

    override fun onPause() {
        super.onPause()
        popupADCurator?.onPause()
        vb.bannerLinearView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            logger.i(viewName = viewTag) { "onDestroy" }
            advertisingLoadAnimation?.stop()
            openADCurator?.release()
            closeADCurator?.release()
            vb.ticketBoxLoadingImage.clearAnimation()
            vb.bannerLinearView.onDestroy()
        } catch (e: Exception) {
            logger.e(viewName = viewTag, throwable = e) { "onDestroy" }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        logger.i(viewName = viewTag) { "onNewIntent" }
        super.onNewIntent(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(view = vb.root, logKey = "view:ticket-box")
        // ALWAYS-FINISH-ACTIVITIES
        if (isAlwaysFinishActivitiesEnabled) {
            val message = MessageDialogHelper.confirm(
                activity = this,
                message = R.string.acb_common_message_always_activity_finished,
                onConfirm = { finish() }
            )
            putDialogView(message)
            message.show(cancelable = false)
            return
        }
        // binding
        vb.ticketBoxLoadingImage.apply {
            advertisingLoadAnimation = background as AnimationDrawable
            advertisingLoadAnimation?.let {
                val colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
                it.colorFilter = colorFilter
            }
        }
        vb.ticketBoxLoadingDescription1.text = getString(R.string.acbsr_string_ticket_box_loading_description_1).toHtml
        vb.ticketBoxAcquirePieces.setOnClickWithDebounce(
            debounceTime = 75L,
            action = { actionTicketCollect() }
        )
        vb.ticketBoxAppOpenButton.setOnClickWithDebounce(
            debounceTime = 125L,
            action = { close(withLaunch = true) }
        )
        vb.ticketBoxActionClose.setOnClickWithDebounce {
            close(withLaunch = false)
        }
        vb.ticketBoxAdClose.setOnClickListener {
            if (popupExposeTime >= (DateTime.now().millis + TicketBoxController.popupCloseDelay)) {
                return@setOnClickListener
            }
            popupADCurator?.release()
            vb.ticketBoxAdContainer.isVisible = false
            vb.ticketBoxAdContainer.post { vb.ticketBoxAdContent.removeAllViews() }
        }
        // STATUS & REQUEST ADVERTISE
        currentStatus = TicketBoxStatus.LOADING

        // region # view model
        ticketBoxViewModel.boxAvailable.observe(this) {
            vb.ticketBoxLoadingContainer.post {
                // animation
                advertisingLoadAnimationPlay = true
                // check result
                when (it) {
                    is ViewModelResult.InProgress -> {
                        loadingView?.show(cancelable = false)
                    }
                    is ViewModelResult.Error -> {
                        loadingView?.dismiss()
                        CoreUtil.showToast(message = R.string.acb_common_message_error)
                        finish()
                    }
                    is ViewModelResult.Complete -> {
                        loadingView?.dismiss()
                        observeTicketBoxAvailable(result = it.result)
                    }
                }
            }
        }
        ticketBoxViewModel.boxUse.observe(this) {
            when (it) {
                is ViewModelResult.InProgress -> {
                    loadingView?.show(cancelable = false)
                }
                is ViewModelResult.Error -> {
                    loadingView?.dismiss()
                    CoreUtil.showToast(message = it.message.takeIfNullOrEmpty { getString(R.string.acb_common_message_error) })
                    finish()
                }
                is ViewModelResult.Complete -> {
                    loadingView?.dismiss()
                    observeTicketBoxUse(it.result)
                }
            }
        }
        // endregion

        // banner
        vb.bannerLinearView.bannerData = ADController.createBannerData(BannerLinearPlacementType.COMMON_320X100)
        vb.bannerLinearView.sourceType = BannerLinearView.SourceType.ROULETTE
        vb.bannerLinearView.requestBanner()

        // request box available
        ticketBoxViewModel.requestBoxAvailable()
    }

    // region 'ticket box' - box available result
    private fun observeTicketBoxAvailable(result: BoxAvailableEntity) {
        // not available
        if (!result.isAvailable) {
            actionAlreadyReceived()
            return
        }
        // pass -> age verification
        if (!result.needAgeVerification) {
            requestTicketBoxTransaction(result.showInterstitial)
            return
        }
        // check -> age verification
        DialogPopupAgeVerifyView.create(
            blockType = RouletteConfig.blockType,
            ownerActivity = this@TicketBoxActivity,
            callback = object : DialogPopupAgeVerifyView.IDialogAction {
                override fun onClose() = finish()
                override fun onAction() {
                    vb.bannerLinearView.refresh()
                    requestTicketBoxTransaction(result.showInterstitial)
                }
            }).show(cancelable = false)
    }
    // endregion


    // region 'ticket box' - reward, use
    private fun requestTicketBoxReward() = ticketBoxViewModel.requestBoxUse()
    private fun observeTicketBoxUse(result: BoxUseEntity) {
        TicketBalanceLiveData.synchronization { _, _ ->
            // box open
            isBoxOpened = true
            // reward text
            with(vb.ticketBoxCompleteText) {
                text = getString(R.string.acbsr_string_ticket_box_complete_title)
                    .format(result.rewardText)
                    .toHtml
            }
            // notification update
            EventContractor.postTicketBoxUpdate(blockType = BlockType.ROULETTE)
            // status
            leakHandler.postDelayed({
                currentStatus = TicketBoxStatus.COMPLETE
                loadingView?.dismiss()
            }, 1500L)
        }
    }
    // endregion

    private fun requestTicketBoxTransaction(showInterstitial: Boolean) {
        if (showInterstitial) {
            requestOpenAdvertise()
        } else {
            requestPopupAdvertise(openADLoader = null)
        }
    }

    private fun requestOpenAdvertise() {
        openADCurator = ADController.createADCuratorQueue(
            activity = this@TicketBoxActivity,
            adQueueType = ADQueueType.TICKET_BOX_OPEN,
            callback = object : ICuratorQueueCallback {
                override fun onLoaded(loader: ADLoaderBase) {
                    logger.i(viewName = viewTag) { "#OPEN -> ADQueue -> onLoaded { loaderType: ${loader.loaderType.name} }" }
                    requestPopupAdvertise(openADLoader = loader)
                }

                override fun onOpened() {
                    logger.i(viewName = viewTag) { "#OPEN -> ADQueue -> onOpened" }
                    advertisingLoadAnimationPlay = false
                }

                override fun onComplete(success: Boolean) {
                    logger.i(viewName = viewTag) { "#OPEN -> ADQueue -> onComplete" }
                    advertisingLoadAnimationPlay = false
                    actionBoxPiecesLoad()
                }

                override fun ondFailed(isBlocked: Boolean) {
                    logger.i(viewName = viewTag) { "#OPEN -> ADQueue -> ondFailed { allowNoAd: ${TicketBoxController.allowNoAd}, isBlocked: $isBlocked }" }
                    advertisingLoadAnimationPlay = false
                    when (TicketBoxController.allowNoAd) {
                        true -> requestPopupAdvertise()
                        false -> when (isBlocked) {
                            true -> actionAdvertisementBlocked()
                            false -> actionAdvertisementInsufficient()
                        }
                    }
                }

                override fun onNeedAgeVerification() {
                    logger.i(viewName = viewTag) { "#OPEN -> ADQueue -> onNeedAgeVerification" }
                    leakHandler.postDelayed({
                        advertisingLoadAnimationPlay = false
                        requestPopupAdvertise()
                    }, 3000)
                }
            }
        )
        openADCurator?.requestAD()
    }

    private fun requestPopupAdvertise(openADLoader: ADLoaderBase? = null) {
        val popupCallback = object : ICuratorPopupCallback {
            override fun oSuccess(adView: View, network: ADNetworkType) {
                logger.i(viewName = viewTag) { "#POPUP -> CuratorPopup -> oSuccess" }
                this@TicketBoxActivity.isExcludeADNetwork = ADController.allowExcludeADNetwork(
                    placementType = ADPlacementType.TICKET_BOX,
                    networkNo = network.value
                )
                advertisingLoadAnimationPlay = false
                vb.ticketBoxAdContainer.isVisible = false
                vb.ticketBoxAdContent.removeAllViews()
                vb.ticketBoxAdContent.addView(adView)
                vb.ticketBoxAdContent.post { openADLoader?.show() ?: run { actionBoxPiecesLoad() } }
            }

            override fun onFailure(isBlocked: Boolean) {
                logger.i(viewName = viewTag) { "#POPUP -> PopupADCoordinator -> onFailure { allowNoAd: ${TicketBoxController.allowNoAd}, isBlocked: $isBlocked }" }
                // animation
                advertisingLoadAnimationPlay = false
                // ALLOW-NO-AD
                when (TicketBoxController.allowNoAd) {
                    true -> {
                        vb.ticketBoxAdContainer.isVisible = false
                        vb.ticketBoxAdContent.removeAllViews()
                        vb.ticketBoxAdContent.post { openADLoader?.show() ?: run { actionBoxPiecesLoad() } }
                    }
                    false -> when (isBlocked) {
                        true -> actionAdvertisementBlocked()
                        false -> actionAdvertisementInsufficient()
                    }
                }
            }

            override fun onNeedAgeVerification() {
                logger.i(viewName = viewTag) { "#POPUP -> PopupADCoordinator -> onNeedAgeVerification" }
                advertisingLoadAnimationPlay = false
                vb.ticketBoxAdContainer.isVisible = false
                vb.ticketBoxAdContent.removeAllViews()
                vb.ticketBoxAdContent.post { actionBoxPiecesLoad() }
            }
        }

        // region popup advertise request
        advertisingLoadAnimationPlay = true
        popupADCurator = ADController.createADCuratorPopup(
            context = this@TicketBoxActivity,
            ADPlacementType.TICKET_BOX,
            callback = popupCallback
        )
        popupADCurator?.requestAD()
        // endregion
    }

    private fun actionBoxPiecesLoad() {
        currentStatus = TicketBoxStatus.ACQUIRES
    }

    private fun actionTicketCollect() {
        if (currentCollectCount < piecesCount) {
            currentCollectCount++
            TicketBoxController.animateCollect(vb.ticketBoxAcquirePieces, currentCollectCount)
            logger.i(viewName = viewTag) { "actionTicketCollect { currentCollectCount:$currentCollectCount, popupExposeCount: $popupExposeCount }" }
            if (!isPopupExposed && currentCollectCount >= popupExposeCount) {
                leakHandler.post { showAdPopup() }
            }
            if (currentCollectCount >= piecesCount) {
                requestTicketBoxReward()
            }
        }
    }

    private fun showAdPopup() {
        isPopupExposed = true
        if (vb.ticketBoxAdContent.childCount > 0) {
            try {
                with(vb.ticketBoxAdPosition) {
                    layoutParams = layoutParams.apply {
                        height = TicketBoxController.popupPosition(allowExcludeADNetwork = isExcludeADNetwork)
                    }
                }
                vb.ticketBoxAdClosePosition.isVisible = isExcludeADNetwork
            } catch (e: Exception) {
                logger.e(viewName = viewTag, throwable = e) { "showPopupAD" }
            } finally {
                popupExposeTime = DateTime.now().millis
                vb.ticketBoxAdContainer.isVisible = true
            }
        }
    }

    fun actionAdvertisementInsufficient() {
        CoreUtil.showToast(message = R.string.acbsr_string_touch_ticket_no_ad)
        leakHandler.postDelayed({ finish() }, 500L)
    }

    fun actionAdvertisementBlocked() {
        CoreUtil.showToast(message = R.string.acb_common_message_advertise_blocked)
        leakHandler.postDelayed({ finish() }, 500L)
    }

    private fun actionAlreadyReceived() {
        val message = MessageDialogHelper.confirm(
            activity = this,
            message = R.string.acbsr_string_ticket_box_already_received_all_your_tickets,
            onConfirm = { finish() }
        )
        putDialogView(message)
        message.show(cancelable = false)
    }

    private fun close(withLaunch: Boolean = false) {
        if (!isBoxOpened) {
            if (withLaunch) {
                EventContractor.postAppLaunchMainActivity(blockType = BlockType.ROULETTE)
            }
            finish()
            return
        }
        // load & show close advertise(interstitial)
        loadingView?.show(cancelable = false)
        closeADCurator = ADController.createADCuratorQueue(
            activity = this@TicketBoxActivity,
            adQueueType = ADQueueType.TICKET_BOX_CLOSE,
            callback = object : ICuratorQueueCallback {
                override fun onLoaded(loader: ADLoaderBase) {
                    loadingView?.dismiss()
                    loader.show()
                }

                override fun onOpened() {
                    loadingView?.dismiss()
                }

                override fun onComplete(success: Boolean) {
                    loadingView?.dismiss()
                    if (withLaunch) {
                        EventContractor.postAppLaunchMainActivity(blockType = BlockType.ROULETTE)
                    }
                    finish()
                }

                override fun ondFailed(isBlocked: Boolean) {
                    loadingView?.dismiss()
                    if (withLaunch) {
                        EventContractor.postAppLaunchMainActivity(blockType = BlockType.ROULETTE)
                    }
                    finish()
                }

                override fun onNeedAgeVerification() {
                    loadingView?.dismiss()
                    if (withLaunch) {
                        EventContractor.postAppLaunchMainActivity(blockType = BlockType.ROULETTE)
                    }
                    finish()
                }
            }
        )
        closeADCurator?.requestAD()
    }
}