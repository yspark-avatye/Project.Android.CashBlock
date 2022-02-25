package com.avatye.cashblock.feature.roulette.presentation.view.main

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.business.EventContractor
import com.avatye.cashblock.base.component.contract.business.SettingContractor
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.domain.entity.game.GamePlayEntity
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult
import com.avatye.cashblock.base.component.support.*
import com.avatye.cashblock.base.component.widget.banner.BannerLinearView
import com.avatye.cashblock.feature.roulette.R
import com.avatye.cashblock.feature.roulette.RouletteConfig.logger
import com.avatye.cashblock.feature.roulette.component.controller.ADController
import com.avatye.cashblock.feature.roulette.component.data.AppConstData
import com.avatye.cashblock.feature.roulette.component.data.PreferenceData
import com.avatye.cashblock.feature.roulette.component.model.entity.BannerLinearPlacementType
import com.avatye.cashblock.feature.roulette.component.model.parcel.RoulettePlayParcel
import com.avatye.cashblock.feature.roulette.databinding.AcbsrActivityRoulettePlayBinding
import com.avatye.cashblock.feature.roulette.presentation.AppBaseActivity
import com.avatye.cashblock.feature.roulette.presentation.viewmodel.roulette.RoulettePlayViewModel
import com.avatye.cashblock.feature.roulette.presentation.viewmodel.ticket.TicketViewModel
import com.avatye.cashblock.feature.roulette.presentation.viewmodel.winner.WinnerViewModel
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlin.random.Random
import com.avatye.cashblock.R as CoreResource

internal class RoulettePlayActivity : AppBaseActivity() {

    companion object {
        fun open(activity: Activity, parcel: RoulettePlayParcel, close: Boolean = false) {
            activity.launch(
                Intent(activity, RoulettePlayActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    putExtra(RoulettePlayParcel.NAME, parcel)
                },
                transition = ActivityTransitionType.NONE.value,
                close = close
            )
        }
    }

    private val appInfo = SettingContractor.appInfoSetting
    private lateinit var parcel: RoulettePlayParcel

    // filter
    private val emoticonFilter = InputFilter { source, start, end, _, _, _ ->
        for (index in start until end) {
            val type = Character.getType(source[index])
            if (type == Character.SURROGATE.toInt()) {
                return@InputFilter ""
            }
        }
        null
    }

    // view-model
    private val ticketViewModel: TicketViewModel by lazy {
        TicketViewModel.create(viewModelStoreOwner = this, lifecycleOwner = this)
    }
    private val roulettePlayViewModel: RoulettePlayViewModel by lazy {
        RoulettePlayViewModel.create(viewModelStoreOwner = this)
    }
    private val winnerViewModel: WinnerViewModel by lazy {
        WinnerViewModel.create(viewModelStoreOwner = this)
    }

    // view
    private val vb: AcbsrActivityRoulettePlayBinding by lazy {
        AcbsrActivityRoulettePlayBinding.inflate((LayoutInflater.from(this)))
    }

    private var isPlaying = false
        set(value) {
            field = value
            when (field) {
                true -> {
                    vb.rouletteActionPlay.setBackgroundResource(CoreResource.drawable.acb_common_rectangle_c1bfd4_r4)
                    vb.roulettePlayText.compoundDrawablesWithIntrinsicBounds(start = R.drawable.acbsr_ic_roulette_ticket_off)
                }
                false -> {
                    vb.rouletteActionPlay.setBackgroundResource(CoreResource.drawable.acb_common_rectangle_ef5350_r4)
                    vb.roulettePlayText.compoundDrawablesWithIntrinsicBounds(start = R.drawable.acbsr_ic_roulette_ticket_on)
                }
            }
        }

    private var possibility: Boolean = false
        set(value) {
            if (value != field) {
                field = value
            }
        }

    override fun onResume() {
        super.onResume()
        vb.bannerLinearView.onResume()
        vb.winnerDiplayView.onResume()
    }

    override fun onPause() {
        super.onPause()
        vb.bannerLinearView.onPause()
        vb.winnerDiplayView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        vb.bannerLinearView.onDestroy()
    }

    override fun onBackPressed() {
        if (isPlaying) {
            return
        }
        if (vb.resultContainer.isVisible) {
            return
        }
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        extraParcel<RoulettePlayParcel>(RoulettePlayParcel.NAME)?.let {
            parcel = it
            setContentViewWith(vb.root, logKey = "view:roulette-view", logParam = hashMapOf("gameID" to it.rouletteID))
            // header
            with(vb.headerView) {
                updateTitleText(it.rouletteName)
                actionClose { close() }
            }
            // ticket consume
            with(vb.ticketConsumeMessage) {
                text = getString(R.string.acbsr_string_roulette_ticket_consume_message)
                    .format(it.rouletteName, it.useTicketAmount)
                    .toHtml
            }
            // roulette play
            with(vb.roulettePlayText) {
                text = getString(R.string.acbsr_string_roulette_play).format(appInfo.rouletteName)
            }
            // edittext filter
            with(vb.resultMessage) {
                filters = arrayOf(emoticonFilter, InputFilter.LengthFilter(10))
            }
            // action
            vb.rouletteActionPlay.setOnClickWithDebounce {
                if (!isPlaying) {
                    when (possibility) {
                        true -> roulettePlayViewModel.play(parcel.rouletteID)
                        false -> CoreUtil.showToast(R.string.acbsr_string_roulette_play_need_more)
                    }
                }
            }
            // banner
            vb.bannerLinearView.bannerData = ADController.createBannerData(BannerLinearPlacementType.COMMON_320X100)
            vb.bannerLinearView.sourceType = BannerLinearView.SourceType.ROULETTE
            vb.bannerLinearView.requestBanner()
            // image load
            requestRouletteImage { observeViewModel() }
        } ?: run {
            CoreUtil.showToast(CoreResource.string.acb_common_message_error)
            leakHandler.postDelayed({ finish() }, 750L)
        }
    }

    private fun requestRouletteImage(callback: () -> Unit) {
        val requestListener = object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                logger.e(viewName = viewTag, throwable = e) { "requestRouletteImage -> bindingRoulette -> onLoadFailed" }
                CoreUtil.showToast(CoreResource.string.acb_common_message_error)
                leakHandler.postDelayed({ finish() }, 1000L)
                return false
            }

            override fun onResourceReady(r: Drawable?, m: Any?, t: Target<Drawable>?, ds: DataSource?, isFirstResource: Boolean): Boolean {
                logger.i(viewName = viewTag) { "requestRouletteImage -> bindingRoulette -> onResourceReady" }
                vb.roulettePin.isVisible = true
                vb.rouletteBoardFrame.setBackgroundResource(R.drawable.acbsr_shape_roulette_back_board)
                callback()
                return false
            }
        }
        glider?.load(parcel.imageUrl)?.listener(requestListener)?.into(vb.rouletteBoard) ?: run {
            CoreUtil.showToast(CoreResource.string.acb_common_message_error)
            leakHandler.postDelayed({ finish() }, 750L)
        }
    }


    private fun observeViewModel() {
        // region #ticket balance
        ticketViewModel.balance.observe(this) {
            with(vb.ticketQuantity) {
                text = getString(R.string.acbsr_string_roulette_ticket_quantity)
                    .format(if (it >= 0) it.toLocaleOver(9999) else "-")
                    .toHtml
            }
            possibility = (it >= parcel.useTicketAmount)
        }
        ticketViewModel.syncBalance { _, _ ->
            logger.i(viewName = viewTag) { "TicketViewModel -> syncBalance" }
        }
        // endregion

        // region #winner board
        winnerViewModel.contents.observe(this) {
            if (it is ViewModelResult.Complete) {
                vb.winnerDiplayView.updateData(winners = it.result)
            }
        }
        winnerViewModel.request()
        // endregion

        // region #roulette play
        roulettePlayViewModel.playResult.observe(this) {
            when (it) {
                is ViewModelResult.InProgress -> {}
                is ViewModelResult.Complete -> requestRoulettePlay(it.result)
                is ViewModelResult.Error -> {
                    val messageDialog = MessageDialogHelper.confirm(
                        activity = this@RoulettePlayActivity,
                        message = if (it.message.isNotEmpty()) {
                            it.message
                        } else {
                            getString(CoreResource.string.acb_common_message_error)
                        }
                    )
                    putDialogView(messageDialog)
                    messageDialog.show(cancelable = false)
                }
            }
        }
        // endregion
    }

    private fun requestRoulettePlay(entity: GamePlayEntity) {
        isPlaying = true
        possibility = false
        val rotationValue = AppConstData.Roulette.rotate + when (entity.isDisplayBoard) {
            true -> AppConstData.Roulette.angleWin
            false -> AppConstData.Roulette.angleLose[Random.nextInt(AppConstData.Roulette.angleLose.size - 1)]
        }
        ObjectAnimator.ofFloat(vb.rouletteBoard, "rotation", 0f, rotationValue).apply {
            interpolator = DecelerateInterpolator()
            duration = 5000
            addListener(object : AnimatorEventCallback() {
                override fun onAnimationEnd(animation: Animator?) {
                    ticketViewModel.syncBalance { _, _ ->
                        isPlaying = false
                        requestRouletteResult(entity)
                    }
                }
            })
        }.start()
    }

    private fun requestRouletteResult(entity: GamePlayEntity) {
        when (entity.isDisplayBoard) {
            true -> {
                with(vb.resultMessage) {
                    text = null
                    resources.getStringArray(R.array.acbsr_string_roulette_result_win_message_hints).let {
                        hint = it[Random.nextInt(it.size - 1)]
                    }
                }
                with(vb.resultConfirm) {
                    setText(CoreResource.string.acb_common_button_confirm)
                    setOnClickListener {
                        actionRouletteWinnerMessage(entity.participateID)
                    }
                }
                vb.resultMessageContainer.isVisible = true
            }
            else -> {
                with(vb.resultConfirm) {
                    setText(CoreResource.string.acb_common_button_close)
                    setOnClickListener {
                        vb.resultContainer.isVisible = false
                    }
                }
                vb.resultMessageContainer.isVisible = false
            }
        }
        vb.resultRewardText.text = entity.rewardText
        vb.resultContainer.isVisible = true
    }

    private fun actionRouletteWinnerMessage(participateID: String) {
        val message = vb.resultMessage.text.toString().trim()
        if (message.isEmpty()) {
            if (PreferenceData.First.isFirstRouletteMessage) {
                PreferenceData.First.update(isFirstRouletteMessage = false)
                CoreUtil.showToast(R.string.acbsr_string_roulette_result_win_message_toast)
                return
            }
        }
        requestRouletteWinnerMessage(participateId = participateID, message)
    }

    private fun requestRouletteWinnerMessage(participateId: String, winnerMessage: String) {
        winnerViewModel.add(participateId = participateId, winnerMessage = winnerMessage) {
            when (it) {
                is ViewModelResult.InProgress -> {
                    loadingView?.show(cancelable = false)
                }
                is ViewModelResult.Error -> {
                    CoreUtil.showToast(
                        it.message.takeIfNullOrEmpty {
                            getString(CoreResource.string.acb_common_message_error)
                        }
                    )
                    vb.resultContainer.isVisible = false
                    loadingView?.dismiss()
                    hideKeyboard()
                }
                is ViewModelResult.Complete -> {
                    loadingView?.dismiss()
                    vb.resultContainer.isVisible = false
                    winnerViewModel.request()
                    EventContractor.postWinnerBoardUpdate(blockType = BlockType.ROULETTE)
                    hideKeyboard()
                }
            }
        }
    }

    private fun hideKeyboard() {
        try {
            val inputMethodManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val windowToken = vb.resultMessage.windowToken
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        } catch (e: Exception) {
            logger.e(viewName = viewTag, throwable = e) { "hideKeyboard" }
        }
    }

    private fun close() {
        if (!isPlaying) {
            try {
                ticketViewModel.balance.removeObservers(this)
                winnerViewModel.contents.removeObservers(this)
            } catch (e: Exception) {
                logger.e(viewName = viewTag, throwable = e) { "close" }
            } finally {
                finish()
            }
        }
    }
}