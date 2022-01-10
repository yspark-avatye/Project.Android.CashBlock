package com.avatye.cashblock.feature.roulette.presentation.view.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avatye.cashblock.base.component.contract.PolicyContract
import com.avatye.cashblock.base.component.contract.RemoteContract
import com.avatye.cashblock.base.component.contract.ViewOpenContract
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.domain.entity.game.GameEntity
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult
import com.avatye.cashblock.base.component.support.*
import com.avatye.cashblock.base.component.widget.decor.GridDividerItemDecoration
import com.avatye.cashblock.base.component.widget.header.HeaderView
import com.avatye.cashblock.base.component.widget.miscellaneous.PlaceHolderRecyclerView
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.feature.roulette.BuildConfig
import com.avatye.cashblock.feature.roulette.MODULE_NAME
import com.avatye.cashblock.feature.roulette.R
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.RouletteConfig.logger
import com.avatye.cashblock.feature.roulette.component.controller.AdvertiseController
import com.avatye.cashblock.feature.roulette.component.controller.MissionController
import com.avatye.cashblock.feature.roulette.component.controller.NotificationController
import com.avatye.cashblock.feature.roulette.component.controller.TicketController
import com.avatye.cashblock.feature.roulette.component.data.PreferenceData
import com.avatye.cashblock.feature.roulette.component.model.entity.BannerLinearPlacementType
import com.avatye.cashblock.feature.roulette.component.model.parcel.RoulettePlayParcel
import com.avatye.cashblock.feature.roulette.component.widget.banner.BannerLinearRewardView
import com.avatye.cashblock.feature.roulette.databinding.AcbsrActivityRouletteMainBinding
import com.avatye.cashblock.feature.roulette.databinding.AcbsrItemRouletteBinding
import com.avatye.cashblock.feature.roulette.presentation.AppBaseActivity
import com.avatye.cashblock.feature.roulette.presentation.view.setting.SettingMainActivity
import com.avatye.cashblock.feature.roulette.presentation.view.ticket.TouchTicketActivity
import com.avatye.cashblock.feature.roulette.presentation.view.ticket.VideoTicketActivity
import com.avatye.cashblock.feature.roulette.presentation.viewmodel.roulette.RouletteListViewModel
import com.avatye.cashblock.feature.roulette.presentation.viewmodel.ticket.TicketViewModel
import com.avatye.cashblock.feature.roulette.presentation.viewmodel.winner.WinnerViewModel

internal class RouletteMainActivity : AppBaseActivity() {

    companion object {
        fun open(activity: Activity, close: Boolean = false) {
            LogHandler.i(moduleName = MODULE_NAME) {
                "RouletteMainActivity -> open"
            }
            activity.launch(
                intent = Intent(activity, RouletteMainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                },
                transition = ActivityTransitionType.NONE.value,
                close = close
            )
        }
    }

    private val appInfo = RemoteContract.appInfoSetting
    private val touchTicketInfo = RemoteContract.touchTicketSetting
    private val videoTicketInfo = RemoteContract.videoTicketSetting

    private val ticketViewModel: TicketViewModel by lazy {
        TicketViewModel.create(viewModelStoreOwner = this, lifecycleOwner = this)
    }
    private val rouletteViewModel: RouletteListViewModel by lazy {
        RouletteListViewModel.create(viewModelStoreOwner = this)
    }
    private val winnerViewModel: WinnerViewModel by lazy {
        WinnerViewModel.create(viewModelStoreOwner = this)
    }

    private val vb: AcbsrActivityRouletteMainBinding by lazy {
        AcbsrActivityRouletteMainBinding.inflate(LayoutInflater.from(this))
    }

    override fun receiveActionInspection() {
        leakHandler.post {
            ViewOpenContract.openInspectionView(
                activity = this@RouletteMainActivity,
                blockCode = RouletteConfig.blockCode,
                close = true
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(vb.root, logKey = "view:roulette-main")

        // region { header }
        with(vb.headerView) {
            this.updateTitleText(appInfo.rouletteName)
            this.actionMore { SettingMainActivity.open(activity = this@RouletteMainActivity, close = false) }
            this.actionClose { finish() }
        }
        // endregion

        // region { campaign & period }
        with(vb.campaignMessage) {
            text = when (appInfo.rouletteCampaign.isEmpty()) {
                true -> getString(R.string.acbsr_string_roulette_campaign_text)
                else -> appInfo.rouletteCampaign
            }.format(appInfo.rouletteName).toHtml
        }
        with(vb.touchTicketPeriod) {
            text = getString(R.string.acbsr_string_ticket_acquire_period)
                .format(TicketController.periodText(touchTicketInfo.period), touchTicketInfo.limitCount)
        }
        with(vb.videoTicketPeriod) {
            text = getString(R.string.acbsr_string_ticket_acquire_period)
                .format(TicketController.periodText(videoTicketInfo.period), videoTicketInfo.limitCount)
        }
        // endregion

        // region { ticket box banner }
        vb.ticketBoxBanner.ownerActivity = this@RouletteMainActivity
        // endregion

        // region { list place holder }
        with(vb.placeHolderRecyclerView) {
            setHasFixedSize(true)
            setLayoutManager(GridLayoutManager(this@RouletteMainActivity, 2))
            setAddItemDecoration(GridDividerItemDecoration(sizeGridSpacingPx = 1.toPX.toInt(), gridSize = 2))
            actionRetry {
                rouletteViewModel.request()
            }
        }
        // endregion

        // region { ViewModel }
        observeTicketViewModel()
        observeRouletteListViewModel()
        observeWinnerViewMode()
        // endregion

        // region { footer }
        vb.footerServiceTerm.setOnClickListener {
            ViewOpenContract.openTermsView(
                activity = this,
                blockCode = RouletteConfig.blockCode,
                headerType = HeaderView.HeaderType.POPUP,
                close = false
            )
        }
        vb.footerSdkVersion.text = "${getString(R.string.acb_common_sdk_version)} ${BuildConfig.X_BUILD_SDK_VERSION_NAME}"
        // endregion

        // region { banner }
        vb.bannerLinearView.bannerData = AdvertiseController.createBannerData(BannerLinearPlacementType.COMMON)
        vb.bannerLinearView.requestBanner()
        initViewRewardBanner()
        // endregion

        // policy
        PolicyContract.notifyUseAAID()
    }

    private fun observeRouletteListViewModel() {
        rouletteViewModel.result.observe(this, {
            when (it) {
                is ViewModelResult.InProgress -> {
                    vb.placeHolderRecyclerView.status = PlaceHolderRecyclerView.Status.LOADING
                    loadingView?.show(cancelable = false)
                }
                is ViewModelResult.Error -> {
                    vb.placeHolderRecyclerView.status = PlaceHolderRecyclerView.Status.ERROR
                    CoreUtil.showToast(R.string.acb_common_message_error)
                    loadingView?.dismiss()
                }
                is ViewModelResult.Complete -> {
                    vb.placeHolderRecyclerView.status = PlaceHolderRecyclerView.Status.CONTENT
                    vb.placeHolderRecyclerView.setAdapter(GameListAdapter(contents = it.result))
                    loadingView?.dismiss()
                }
            }
        })
        // synchronization
        rouletteViewModel.request()
    }

    private fun observeTicketViewModel() {
        // balance
        ticketViewModel.balance.observe(this, {
            with(vb.ticketBalance) {
                text = getString(R.string.acbsr_string_roulette_ticket_quantity)
                    .format(if (it >= 0) it.toLocaleOver(9999) else "-")
                    .toHtml
            }
        })
        // touch ticket
        ticketViewModel.touchTicket.observe(this, {
            vb.touchTicketInfo.alpha = if (it > 0) 1F else 0.3F
            vb.touchTicketLimitCount.text = " / ${ticketViewModel.touchTicketLimit}"
            vb.touchTicketReceivedCount.text = if (it >= 0) it.toString() else "-"
            vb.touchTicketAction.setOnClickWithDebounce(debounceTime = 1200L) {
                when (it > 0) {
                    true -> TouchTicketActivity.open(activity = this@RouletteMainActivity)
                    false -> CoreUtil.showToast(R.string.acbsr_string_ticket_already_received)
                }
            }
        })
        // video ticket
        ticketViewModel.videoTicket.observe(this, {
            vb.videoTicketInfo.alpha = if (it > 0) 1F else 0.3F
            vb.videoTicketLimitCount.text = " / ${ticketViewModel.videoTicketLimit}"
            vb.videoTicketReceivedCount.text = if (it >= 0) it.toString() else "-"
            vb.videoTicketAction.setOnClickWithDebounce(debounceTime = 1200L) {
                when (it > 0) {
                    true -> VideoTicketActivity.open(activity = this@RouletteMainActivity)
                    false -> CoreUtil.showToast(R.string.acbsr_string_ticket_already_received)
                }
            }
        })
        // synchronization
        ticketViewModel.synchronization()
    }

    private fun observeWinnerViewMode() {
        winnerViewModel.contents.observe(this, {
            if (it is ViewModelResult.Complete) {
                vb.winnerMessageBoard.updateData(winners = it.result)
            }
        })
        winnerViewModel.request()
    }

    override fun receiveActionUnAuthorized() {
        leakHandler.post {
            CoreUtil.showToast(R.string.acb_common_message_session_expire)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // winner board
        vb.winnerMessageBoard.onResume()
        // banners
        vb.bannerRewardView.onResume()
        vb.ticketBoxBanner.onResume()
        vb.bannerLinearView.onResume()
        // popup notice polling
        popupPolling()
    }

    override fun onPause() {
        super.onPause()
        // winner board
        vb.winnerMessageBoard.onPause()
        // banners
        vb.bannerRewardView.onPause()
        vb.bannerLinearView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        vb.bannerLinearView.onDestroy()
        vb.bannerRewardView.onDestroy()
    }

    private fun popupPolling() {
        if (!isShowingDialogView) {
            RouletteConfig.popupNoticeController.requestPopups(ownerActivity = this@RouletteMainActivity) {
                logger.i { "$viewTag -> popupPolling -> requestPopups -> complete" }
                MissionController.Attendance.sync(ownerActivity = this@RouletteMainActivity) {
                    logger.i { "$viewTag -> popupPolling -> mission -> attendance -> complete" }
                    if (RemoteContract.appInfoSetting.allowTicketBox) {
                        // notification -> induce popup
                        // -> battery optimize popup
                        when (NotificationController.Host.useHostNotification) {
                            true -> NotificationController.Host.induceNotificationService(ownerActivity = this@RouletteMainActivity)
                            false -> NotificationController.SDK.induceNotificationService(ownerActivity = this@RouletteMainActivity)
                        }
                    }
                }
            }
        }
    }

    private fun initViewRewardBanner() {
        vb.bannerRewardPointContainer.isVisible = vb.bannerRewardView.hasReward
        vb.bannerRewardView.rewardCallback = object : BannerLinearRewardView.RewardCallback {
            override fun onReward(hasReward: Boolean) {
                logger.i { "$viewTag -> initViewRewardBanner -> onReward { hasReward: $hasReward }" }
                val visible = vb.bannerRewardView.isVisible && hasReward
                if (visible) {
                    with(vb.bannerRewardPoint) {
                        text = context.getString(R.string.acbsr_string_linear_reward_banner_point)
                            .format(
                                "${appInfo.rouletteName} 티켓",
                                PreferenceData.BannerReward.amount
                            ).toHtml
                    }
                }
                vb.bannerRewardPointContainer.isVisible = visible
            }

            override fun onAdFail() {
                logger.i { "$viewTag -> initViewRewardBanner -> onAdFail" }
                vb.bannerRewardView.isVisible = false
                vb.bannerRewardPointContainer.isVisible = false
            }
        }
    }


    private inner class GameListAdapter(private val contents: MutableList<GameEntity>) : RecyclerView.Adapter<GameListAdapter.ItemViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            return ItemViewHolder(AcbsrItemRouletteBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            holder.viewBind(entity = contents[position])
        }

        override fun getItemCount() = contents.size

        inner class ItemViewHolder(private val ib: AcbsrItemRouletteBinding) : RecyclerView.ViewHolder(ib.root) {
            fun viewBind(entity: GameEntity) {
                // ICON
                with(ib.itemRouletteIcon) {
                    if (entity.iconUrl.isNotEmpty()) {
                        isVisible = true
                        glider?.load(entity.iconUrl)?.into(this)
                    } else {
                        isVisible = false
                    }
                }
                // NAME
                with(ib.itemRouletteName) {
                    text = entity.title
                    isVisible = entity.title.isNotEmpty()
                }
                // NEED-TICKET
                with(ib.itemRouletteTicketAmount) {
                    text = entity.useTicketAmount.toString()
                    isVisible = entity.useTicketAmount > 0
                }
                // ACTION
                if (entity.gameID.isNotEmpty()) {
                    ib.root.setOnClickListener {
                        val parcel = RoulettePlayParcel(
                            rouletteID = entity.gameID,
                            rouletteName = entity.title,
                            useTicketAmount = entity.useTicketAmount,
                            iconUrl = entity.iconUrl,
                            imageUrl = entity.imageUrl
                        )
                        RoulettePlayActivity.open(this@RouletteMainActivity, parcel = parcel, close = false)
                    }
                }
            }
        }
    }
}