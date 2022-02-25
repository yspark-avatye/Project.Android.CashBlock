package com.avatye.cashblock.feature.roulette.presentation.view.ticket

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
import com.avatye.cashblock.base.component.contract.business.SettingContractor
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.domain.entity.ticket.TicketBalanceEntity
import com.avatye.cashblock.base.component.domain.entity.ticket.TicketRequestEntity
import com.avatye.cashblock.base.component.domain.entity.ticket.TicketType
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
import com.avatye.cashblock.feature.roulette.component.controller.TicketController
import com.avatye.cashblock.feature.roulette.component.model.entity.ADPlacementType
import com.avatye.cashblock.feature.roulette.component.model.entity.ADQueueType
import com.avatye.cashblock.feature.roulette.component.model.entity.BannerLinearPlacementType
import com.avatye.cashblock.feature.roulette.databinding.AcbsrActivityTouchTicketBinding
import com.avatye.cashblock.feature.roulette.presentation.AppBaseActivity
import com.avatye.cashblock.feature.roulette.presentation.viewmodel.ticket.TicketTransactionViewModel
import com.avatye.cashblock.feature.roulette.presentation.viewmodel.ticket.TicketViewModel
import org.joda.time.DateTime
import com.avatye.cashblock.R as CoreResource

internal class TouchTicketActivity : AppBaseActivity() {

    companion object {
        fun open(activity: Activity, close: Boolean = false) {
            activity.launch(
                Intent(activity, TouchTicketActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                },
                transition = ActivityTransitionType.NONE.value,
                close = close
            )
        }
    }

    private val appInfoSetting = SettingContractor.appInfoSetting
    private val touchTicketSetting = SettingContractor.touchTicketSetting

    // view-binding
    private val vb: AcbsrActivityTouchTicketBinding by lazy {
        AcbsrActivityTouchTicketBinding.inflate(LayoutInflater.from(this))
    }

    // view-model
    private val ticketTransactionViewModel: TicketTransactionViewModel by lazy {
        TicketTransactionViewModel.create(ticketType = TicketType.TOUCH, viewModelStoreOwner = this)
    }
    private val ticketViewModel: TicketViewModel by lazy {
        TicketViewModel.create(viewModelStoreOwner = this, lifecycleOwner = this)
    }

    private val rouletteName: String by lazy { appInfoSetting.rouletteName }
    private val limitCount: Int by lazy { touchTicketSetting.limitCount }
    private val periodText: String by lazy { TicketController.periodText(touchTicketSetting.period) }
    private val piecesCount: Int by lazy { TicketController.TouchTicketAcquire.piecesCount }
    private val popupExposeCount: Int by lazy { TicketController.TouchTicketAcquire.popupExposeCount }

    private enum class TicketAcquireStatus { LOADING, ACQUIRES, COMPLETE }

    private var popupExposeTime = 0L
    private var isExcludeADNetwork = false
    private var isTicketAcquired = false
    private var isPopupExposed: Boolean = false
    private var loadingAnimation: AnimationDrawable? = null
    private var isCloseable: Boolean = true

    private var currentCollectCount = 0
        set(value) {
            if (value <= TicketController.TouchTicketAcquire.piecesCount) {
                field = value
            }
        }

    private var receivableCount: Int = 0
        set(value) {
            if (field != value) {
                field = value
            }
            with(vb.touchTicketAcquireButton) {
                text = getString(R.string.acbsr_string_touch_ticket_acquirable_count).format(field, limitCount)
                isEnabled = field > 0
            }
        }

    private var currentStatus = TicketAcquireStatus.LOADING
        set(value) {
            if (value != field) {
                field = value
                when (field) {
                    TicketAcquireStatus.LOADING -> {
                        vb.touchTicketAcquireContainer.isVisible = false
                        vb.touchTicketCompleteContainer.isVisible = false
                        // LOADING
                        vb.touchTicketLoadingContainer.isVisible = true
                    }
                    TicketAcquireStatus.ACQUIRES -> {
                        loadingAnimation?.stop()
                        vb.touchTicketLoadingContainer.isVisible = false
                        vb.touchTicketCompleteContainer.isVisible = false
                        // ACQUIRES
                        vb.touchTicketAcquireContainer.isVisible = true
                        vb.touchTicketAcquirePieces.setImageResource(TicketController.TouchTicketAcquire.pieces(0))
                        vb.touchTicketAcquirePieces.animateScaleIn(
                            interpolator = OvershootInterpolator(),
                            duration = 500L,
                            callback = {
                                isPopupExposed = false
                                currentCollectCount = 0
                            }
                        )
                    }
                    TicketAcquireStatus.COMPLETE -> {
                        isCloseable = false
                        loadingAnimation?.stop()
                        vb.touchTicketLoadingContainer.isVisible = false
                        vb.touchTicketAcquireContainer.isVisible = false
                        // COMPLETE
                        vb.touchTicketCompleteContainer.isVisible = true
                        vb.touchTicketCompleteText.isVisible = false
                        vb.touchTicketCompletePeriod.isVisible = false
                        vb.touchTicketAcquireButton.isVisible = false
                        TicketController.TouchTicketAcquire.animateComplete(vb.touchTicketCompleteImage) {
                            isCloseable = true
                            vb.touchTicketAcquireButton.isVisible = true
                            vb.touchTicketCompletePeriod.isVisible = true
                            vb.touchTicketCompleteText.isVisible = true
                        }
                    }
                }
            }
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
        vb.touchTicketAdContainer.isVisible = false
    }

    override fun onPause() {
        super.onPause()
        popupADCurator?.onPause()
        vb.bannerLinearView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            openADCurator?.release()
            closeADCurator?.release()
            vb.touchTicketLoadingImage.clearAnimation()
            vb.bannerLinearView.onDestroy()
        } catch (e: Exception) {
            logger.e(viewName = viewTag, throwable = e) { "onDestroy" }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(view = vb.root, logKey = "view:touch-ticket")

        // region # always-finish-activities
        if (isAlwaysFinishActivitiesEnabled) {
            val message = MessageDialogHelper.confirm(
                activity = this,
                message = CoreResource.string.acb_common_message_always_activity_finished,
                onConfirm = { finish() }
            )
            putDialogView(message)
            message.show(cancelable = false)
            return
        }
        // endregion

        // region # loading-animation
        vb.touchTicketLoadingImage.apply {
            loadingAnimation = background as AnimationDrawable
            loadingAnimation?.let {
                val colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
                it.colorFilter = colorFilter
            }
        }
        // endregion

        // region # period-text
        getString(R.string.acbsr_string_touch_ticket_period).format(periodText, limitCount).let {
            vb.touchTicketLoadingPeriod.text = it.toHtml
            vb.touchTicketAcquirePeriod.text = it.toHtml
            vb.touchTicketCompletePeriod.text = it.toHtml
        }
        // endregion

        // region # back-fill
        with(vb.adPopupBackFill) {
            text = getString(R.string.acbsr_string_touch_ticket_ad_popup_default_message)
                .format(periodText, limitCount, rouletteName)
                .toHtml
        }
        // endregion

        // region # click-event
        vb.touchTicketActionClose.setOnClickWithDebounce(debounceTime = 750L) { close() }
        vb.touchTicketAcquirePieces.setOnClickWithDebounce(debounceTime = 75L) { actionTicketCollect() }
        vb.touchTicketAcquireButton.setOnClickWithDebounce { requestViewDataTransaction() }
        vb.touchTicketAdClose.setOnClickListener {
            if (popupExposeTime >= (DateTime.now().millis + TicketController.TouchTicketAcquire.popupCloseDelay)) {
                return@setOnClickListener
            }
            popupADCurator?.release()
            vb.touchTicketAdContainer.isVisible = false
            vb.touchTicketAdContainer.post {
                vb.touchTicketAdContent.removeAllViews()
            }
        }
        // endregion

        // region # banner-linear
        vb.bannerLinearView.bannerData = ADController.createBannerData(BannerLinearPlacementType.TOUCH_TICKET)
        vb.bannerLinearView.sourceType = BannerLinearView.SourceType.ROULETTE
        vb.bannerLinearView.requestBanner()
        // endregion

        // region # view-model observe
        ticketTransactionViewModel.transaction.observe(this, { observeTransaction(it) })
        ticketTransactionViewModel.ticketing.observe(this, { observeTicketing(it) })
        ticketViewModel.touchTicket.observe(this, { receivableCount = it })
        ticketViewModel.syncTouchTicket { success, syncValue ->
            if (success) {
                if (syncValue == 0) {
                    actionAlreadyReceived()
                } else {
                    receivableCount = syncValue
                    requestViewDataTransaction()
                }
            } else {
                CoreUtil.showToast(CoreResource.string.acb_common_message_error)
                leakHandler.postDelayed({ finish() }, 300L)
            }
        }
        // endregion
    }

    private fun requestViewDataTransaction() = ticketTransactionViewModel.requestTransaction()

    private fun requestViewDataTicketing() = ticketTransactionViewModel.requestTicketing()

    private fun observeTransaction(model: ViewModelResult<TicketRequestEntity>) {
        when (model) {
            is ViewModelResult.InProgress -> {
                currentStatus = TicketAcquireStatus.LOADING
                loadingView?.show(cancelable = false)
            }
            is ViewModelResult.Error -> {
                val messageDialog = MessageDialogHelper.confirm(
                    this@TouchTicketActivity,
                    message = CoreResource.string.acb_common_message_error,
                    onConfirm = { finish() }
                )
                putDialogView(messageDialog)
                loadingView?.dismiss()
                messageDialog.show(cancelable = false)
            }
            is ViewModelResult.Complete -> {
                loadingView?.dismiss()
                if (model.result.needAgeVerification) {
                    DialogPopupAgeVerifyView.create(
                        blockType = RouletteConfig.blockType,
                        ownerActivity = this@TouchTicketActivity,
                        callback = object : DialogPopupAgeVerifyView.IDialogAction {
                            override fun onClose() = finish()
                            override fun onAction() {
                                vb.bannerLinearView.refresh()
                                requestOpenAdvertise()
                            }
                        }).show(cancelable = false)
                } else {
                    requestOpenAdvertise()
                }
            }
        }
    }

    private fun observeTicketing(model: ViewModelResult<TicketBalanceEntity>) {
        when (model) {
            is ViewModelResult.InProgress -> {
                loadingView?.show(cancelable = false)
            }
            is ViewModelResult.Error -> {
                CoreUtil.showToast(model.message.takeIfNullOrEmpty { getString(CoreResource.string.acb_common_message_error) })
                loadingView?.dismiss()
                finish()
            }
            is ViewModelResult.Complete -> {
                ticketViewModel.synchronization {
                    currentStatus = TicketAcquireStatus.COMPLETE
                    isTicketAcquired = true
                    loadingView?.dismiss()
                }
            }
        }
    }

    private fun requestOpenAdvertise() {
        loadingAnimation?.start()
        openADCurator = ADController.createADCuratorQueue(
            activity = this@TouchTicketActivity,
            adQueueType = ADQueueType.TOUCH_TICKET_OPEN,
            callback = object : ICuratorQueueCallback {
                override fun onLoaded(loader: ADLoaderBase) {
                    logger.i(viewName = viewTag) { "#OPEN -> ADQueue -> onLoaded { loaderType: ${loader.loaderType.name} }" }
                    requestPopupAdvertise(openADLoader = loader)
                }

                override fun onOpened() {
                    logger.i(viewName = viewTag) { "#OPEN -> ADQueue -> onOpened" }
                    loadingAnimation?.stop()
                }

                override fun onComplete(success: Boolean) {
                    logger.i(viewName = viewTag) { "#OPEN -> ADQueue -> onComplete" }
                    loadingAnimation?.stop()
                    actionTicketPiecesLoad()
                }

                override fun ondFailed(isBlocked: Boolean) {
                    logger.i(viewName = viewTag) { "#OPEN -> ADQueue -> ondFailed { allowNoAd: ${TicketController.TouchTicketAcquire.allowNoAd}, isBlocked: $isBlocked }" }
                    loadingAnimation?.stop()
                    when (TicketController.TouchTicketAcquire.allowNoAd) {
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
                        loadingAnimation?.stop()
                        requestPopupAdvertise()
                    }, 3000)
                }
            })
        openADCurator?.requestAD()
    }

    private fun requestPopupAdvertise(openADLoader: ADLoaderBase? = null) {
        loadingAnimation?.start()
        val curatorPopupCallback = object : ICuratorPopupCallback {
            override fun oSuccess(adView: View, network: ADNetworkType) {
                logger.i(viewName = viewTag) { "CuratorPopup -> oSuccess" }
                loadingAnimation?.stop()
                this@TouchTicketActivity.isExcludeADNetwork = ADController.allowExcludeADNetwork(ADPlacementType.TOUCH_TICKET, network.value)
                vb.touchTicketAdContainer.isVisible = false
                vb.touchTicketAdContent.removeAllViews()
                vb.touchTicketAdContent.addView(adView)
                vb.touchTicketAdContent.post {
                    openADLoader?.show() ?: run {
                        actionTicketPiecesLoad()
                    }
                }
            }

            override fun onFailure(isBlocked: Boolean) {
                logger.i(viewName = viewTag) { "#POPUP -> PopupADCoordinator -> onFailure { allowNoAd: ${TicketController.TouchTicketAcquire.allowNoAd}, isBlocked: $isBlocked }" }
                loadingAnimation?.stop()
                when (TicketController.TouchTicketAcquire.allowNoAd) {
                    true -> {
                        vb.touchTicketAdContainer.isVisible = false
                        vb.touchTicketAdContent.removeAllViews()
                        vb.touchTicketAdContent.post { openADLoader?.show() ?: run { actionTicketPiecesLoad() } }
                    }
                    false -> when (isBlocked) {
                        true -> actionAdvertisementBlocked()
                        false -> actionAdvertisementInsufficient()
                    }
                }
            }

            override fun onNeedAgeVerification() {
                logger.i(viewName = viewTag) { "#POPUP -> PopupADCoordinator -> onNeedAgeVerification" }
                loadingAnimation?.stop()
                vb.touchTicketAdContainer.isVisible = false
                vb.touchTicketAdContent.removeAllViews()
                vb.touchTicketAdContent.post {
                    actionTicketPiecesLoad()
                }
            }
        }
        popupADCurator = ADController.createADCuratorPopup(context = this@TouchTicketActivity, placementType = ADPlacementType.TOUCH_TICKET, callback = curatorPopupCallback)
        popupADCurator?.requestAD()
    }

    private fun actionTicketPiecesLoad() {
        currentStatus = TicketAcquireStatus.ACQUIRES
    }

    private fun actionTicketCollect() {
        if (currentCollectCount < piecesCount) {
            currentCollectCount++
            TicketController.TouchTicketAcquire.animateCollect(vb.touchTicketAcquirePieces, currentCollectCount)
            logger.i(viewName = viewTag) { "actionTicketCollect { currentCollectCount:$currentCollectCount, popupExposeCount: $popupExposeCount }" }
            if (!isPopupExposed && currentCollectCount >= popupExposeCount) {
                leakHandler.post { showAdPopup() }
            }
            if (currentCollectCount >= piecesCount) {
                requestViewDataTicketing()
            }
        }
    }

    private fun showAdPopup() {
        isPopupExposed = true
        if (vb.touchTicketAdContent.childCount > 0) {
            try {
                with(vb.touchTicketAdPosition) {
                    layoutParams = layoutParams.apply {
                        height = TicketController.TouchTicketAcquire.popupPosition(allowExcludeADNetwork = isExcludeADNetwork)
                    }
                }
                vb.touchTicketAdClosePosition.isVisible = isExcludeADNetwork
            } catch (e: Exception) {
                logger.e(viewName = viewTag, throwable = e) { "showAdPopup()" }
            } finally {
                popupExposeTime = DateTime.now().millis
                vb.touchTicketAdContainer.isVisible = true
            }
        }
    }

    private fun actionAdvertisementInsufficient() {
        CoreUtil.showToast(R.string.acbsr_string_touch_ticket_no_ad)
        leakHandler.postDelayed({ finish() }, 500L)
    }

    fun actionAdvertisementBlocked() {
        CoreUtil.showToast(CoreResource.string.acb_common_message_advertise_blocked)
        leakHandler.postDelayed({ finish() }, 500L)
    }

    private fun actionAlreadyReceived() {
        val message = MessageDialogHelper.confirm(
            activity = this,
            message = R.string.acbsr_string_touch_ticket_already_received_all_your_tickets,
            onConfirm = { finish() }
        )
        putDialogView(message)
        message.show(cancelable = false)
    }

    private fun close() {
        if (!isTicketAcquired) {
            finish()
            return
        }
        if (isCloseable) {
            // load & show close advertise(interstitial)
            loadingView?.show(cancelable = false)
            closeADCurator = ADController.createADCuratorQueue(
                activity = this,
                adQueueType = ADQueueType.TOUCH_TICKET_CLOSE,
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
                        finish()
                    }

                    override fun ondFailed(isBlocked: Boolean) {
                        loadingView?.dismiss()
                        finish()
                    }

                    override fun onNeedAgeVerification() {
                        loadingView?.dismiss()
                        finish()
                    }
                }
            )
            closeADCurator?.requestAD()
        }
    }
}