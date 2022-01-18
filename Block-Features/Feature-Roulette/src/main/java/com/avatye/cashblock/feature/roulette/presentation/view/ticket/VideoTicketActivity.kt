package com.avatye.cashblock.feature.roulette.presentation.view.ticket

import android.app.Activity
import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import com.avatye.cashblock.base.component.contract.business.AccountContractor
import com.avatye.cashblock.base.component.contract.business.SettingContractor
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.domain.entity.ticket.TicketBalanceEntity
import com.avatye.cashblock.base.component.domain.entity.ticket.TicketRequestEntity
import com.avatye.cashblock.base.component.domain.entity.ticket.TicketType
import com.avatye.cashblock.base.component.domain.entity.user.AgeVerifiedType
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult
import com.avatye.cashblock.base.component.support.*
import com.avatye.cashblock.base.component.widget.dialog.DialogPopupAgeVerifyView
import com.avatye.cashblock.base.library.ad.curator.queue.CuratorQueue
import com.avatye.cashblock.base.library.ad.curator.queue.ICuratorQueueCallback
import com.avatye.cashblock.base.library.ad.curator.queue.loader.ADLoaderBase
import com.avatye.cashblock.feature.roulette.R
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.RouletteConfig.logger
import com.avatye.cashblock.feature.roulette.component.controller.AdvertiseController
import com.avatye.cashblock.feature.roulette.component.controller.TicketController
import com.avatye.cashblock.feature.roulette.component.model.entity.ADQueueType
import com.avatye.cashblock.feature.roulette.component.model.entity.BannerLinearPlacementType
import com.avatye.cashblock.feature.roulette.databinding.AcbsrActivityVidoeTicketBinding
import com.avatye.cashblock.feature.roulette.presentation.AppBaseActivity
import com.avatye.cashblock.feature.roulette.presentation.viewmodel.ticket.TicketTransactionViewModel
import com.avatye.cashblock.feature.roulette.presentation.viewmodel.ticket.TicketViewModel
import com.avatye.cashblock.R as CoreResource

internal class VideoTicketActivity : AppBaseActivity() {

    companion object {
        fun open(activity: Activity, close: Boolean = false) {
            activity.launch(
                Intent(activity, VideoTicketActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                },
                transition = ActivityTransitionType.NONE.value,
                close = close
            )
        }
    }

    private val videoTicketSetting = SettingContractor.videoTicketSetting

    private enum class TicketAcquireStatus { LOADING, TICKETING, COMPLETE }

    // view-binding
    private val vb: AcbsrActivityVidoeTicketBinding by lazy {
        AcbsrActivityVidoeTicketBinding.inflate(LayoutInflater.from(this))
    }

    // view-model
    private val ticketTransactionViewModel: TicketTransactionViewModel by lazy {
        TicketTransactionViewModel.create(ticketType = TicketType.VIDEO, viewModelStoreOwner = this)
    }
    private val ticketViewModel: TicketViewModel by lazy {
        TicketViewModel.create(viewModelStoreOwner = this, lifecycleOwner = this)
    }

    private var isCloseable: Boolean = true
    private var isTicketAcquired: Boolean = false
    private val limitCount: Int by lazy { videoTicketSetting.limitCount }
    private val periodText: String by lazy { TicketController.periodText(videoTicketSetting.period) }

    // advertise curator
    private var openADCurator: CuratorQueue? = null
    private var closeADCurator: CuratorQueue? = null

    private var loadingAnimation: AnimationDrawable? = null
    private var receivableCount: Int = 0
        set(value) {
            if (field != value) {
                field = value
            }
            with(vb.videoTicketAcquireButton) {
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
                        vb.videoTicketContainerLoading.isVisible = true
                        vb.videoTicketContainerAcquire.isVisible = false
                        vb.videoTicketAcquireButton.visibility = View.INVISIBLE
                    }
                    TicketAcquireStatus.TICKETING -> {
                        loadingAnimation?.start()
                        vb.videoTicketAcquireDescription.setText(R.string.acbsr_string_video_ticket_ready)
                        vb.videoTicketContainerLoading.isVisible = true
                        vb.videoTicketContainerAcquire.isVisible = false
                        vb.videoTicketAcquireButton.visibility = View.INVISIBLE
                    }
                    TicketAcquireStatus.COMPLETE -> {
                        isCloseable = false
                        loadingAnimation?.stop()
                        vb.videoTicketAcquirePieces.setImageResource(R.drawable.acbsr_ic_puzzle_tk10)
                        vb.videoTicketAcquireDescription.setText(R.string.acbsr_string_video_ticket_complete)
                        vb.videoTicketContainerLoading.isVisible = false
                        vb.videoTicketContainerAcquire.isVisible = true
                        vb.videoTicketAcquireButton.visibility = View.INVISIBLE
                        TicketController.VideoTicketAcquire.animateComplete(vb.videoTicketAcquirePieces) {
                            isCloseable = true
                            vb.videoTicketAcquireButton.isVisible = true
                        }
                    }
                }
            }
        }

    override fun onBackPressed() {
        return
    }

    override fun onResume() {
        super.onResume()
        // coordinator
        openADCurator?.onResume()
        closeADCurator?.onResume()
        // linear banner
        vb.bannerLinearView.onResume()
    }

    override fun onPause() {
        super.onPause()
        // animation
        loadingAnimation?.stop()
        // coordinator
        openADCurator?.onPause()
        closeADCurator?.onPause()
        // linear banner
        vb.bannerLinearView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            // coordinator
            openADCurator?.release()
            closeADCurator?.release()
            // linear banner
            vb.bannerLinearView.onDestroy()
        } catch (e: Exception) {
            logger.e(viewName = viewTag, throwable = e) { "$viewTag -> onDestroy" }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(view = vb.root, logKey = "view:video-ticket")

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
        vb.videoTicketLoadingImage.apply {
            loadingAnimation = background as AnimationDrawable
            loadingAnimation?.let {
                val colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
                it.colorFilter = colorFilter
            }
        }
        // endregion

        // region # period-text
        getString(R.string.acbsr_string_touch_ticket_period).format(periodText, limitCount).let {
            vb.videoTicketLoadingPeriod.text = it.toHtml
            vb.videoTicketAcquirePeriod.text = it.toHtml
        }
        // endregion

        // region # click-event
        vb.videoTicketActionClose.setOnClickWithDebounce(debounceTime = 750L) { close() }
        vb.videoTicketAcquireButton.setOnClickWithDebounce { requestViewDataTransaction() }
        // endregion

        // region # banner-linear
        vb.bannerLinearView.bannerData = AdvertiseController.createBannerData(BannerLinearPlacementType.VIDEO_TICKET)
        vb.bannerLinearView.requestBanner()
        // endregion


        // region # view-model observe
        ticketTransactionViewModel.transaction.observe(this, { observeTransaction(it) })
        ticketTransactionViewModel.ticketing.observe(this, { observeTicketing(it) })
        ticketViewModel.videoTicket.observe(this, { receivableCount = it })
        // endregion


        // region # check-age-verify
        if (AccountContractor.ageVerified == AgeVerifiedType.VERIFIED) {
            requestVideoTicketSync()
        } else {
            DialogPopupAgeVerifyView.create(
                blockType = RouletteConfig.blockType,
                ownerActivity = this@VideoTicketActivity,
                callback = object : DialogPopupAgeVerifyView.IDialogAction {
                    override fun onAction() {
                        vb.bannerLinearView.refresh()
                        requestVideoTicketSync()
                    }

                    override fun onClose() {
                        finish()
                    }
                }).show(cancelable = false)
        }
        // endregion
    }

    private fun requestVideoTicketSync() {
        ticketViewModel.syncVideoTicket { success, syncValue ->
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
                    this@VideoTicketActivity,
                    message = CoreResource.string.acb_common_message_error,
                    onConfirm = { finish() }
                )
                putDialogView(messageDialog)
                loadingView?.dismiss()
                messageDialog.show(cancelable = false)
            }
            is ViewModelResult.Complete -> {
                loadingView?.dismiss()
                requestOpenAdvertise()
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
        openADCurator = AdvertiseController.createADCuratorQueue(
            activity = this@VideoTicketActivity,
            adQueueType = ADQueueType.VIDEO_TICKET_OPEN,
            callback = object : ICuratorQueueCallback {
                override fun onLoaded(loader: ADLoaderBase) {
                    logger.i(viewName = viewTag) { "#OPEN -> ADQueue -> onLoaded { loaderType: ${loader.loaderType.name} }" }
                    loadingAnimation?.stop()
                    loader.show()
                }

                override fun onOpened() {
                    logger.i(viewName = viewTag) { "#OPEN -> ADQueue -> onOpened" }
                    loadingAnimation?.stop()
                }

                override fun onComplete(success: Boolean) {
                    loadingAnimation?.stop()
                    if (success) {
                        // ticketing
                        loadingView?.show(cancelable = false)
                        leakHandler.post { currentStatus = TicketAcquireStatus.TICKETING }
                        leakHandler.postDelayed({ requestViewDataTicketing() }, 750L)
                    } else {
                        CoreUtil.showToast(R.string.acbsr_string_video_ticket_video_stopped)
                        leakHandler.postDelayed({ finish() }, 300L)
                    }
                }

                override fun ondFailed(isBlocked: Boolean) {
                    loadingAnimation?.stop()
                    when (isBlocked) {
                        true -> actionAdvertisementBlocked()
                        false -> actionAdvertisementInsufficient()
                    }
                }

                override fun onNeedAgeVerification() {
                    leakHandler.postDelayed({
                        loadingAnimation?.stop()
                        MessageDialogHelper.confirm(
                            activity = this@VideoTicketActivity,
                            message = CoreResource.string.acb_common_message_ad_age_not_support,
                            onConfirm = { finish() }
                        ).apply {
                            putDialogView(this)
                        }.show(cancelable = false)
                    }, 1500)
                }
            }
        )
        openADCurator?.requestAD()
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
        if (currentStatus == TicketAcquireStatus.TICKETING) {
            return
        }
        if (!isTicketAcquired) {
            finish()
            return
        }
        if (isCloseable) {
            // load & show close advertise(interstitial)
            loadingView?.show(cancelable = false)
            closeADCurator = AdvertiseController.createADCuratorQueue(
                activity = this,
                adQueueType = ADQueueType.VIDEO_TICKET_CLOSE,
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