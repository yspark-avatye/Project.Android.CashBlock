package com.avatye.cashblock.feature.roulette.component.widget.banner

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.business.EventContractor
import com.avatye.cashblock.base.component.contract.business.SettingContractor
import com.avatye.cashblock.base.component.support.setOnClickWithDebounce
import com.avatye.cashblock.feature.roulette.component.controller.NotificationController
import com.avatye.cashblock.feature.roulette.component.controller.TicketBoxController
import com.avatye.cashblock.feature.roulette.databinding.AcbsrWidgetBannerTicketBoxBinding
import com.avatye.cashblock.feature.roulette.presentation.AppBaseActivity

internal class BannerTicketBoxView(context: Context, attrs: AttributeSet? = null) : RelativeLayout(context, attrs) {

    var ownerActivity: AppBaseActivity? = null

    private val vb: AcbsrWidgetBannerTicketBoxBinding by lazy {
        AcbsrWidgetBannerTicketBoxBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun onResume() {
        if (SettingContractor.appInfoSetting.allowTicketBox) {
            isVisible = TicketBoxController.hasTicketBox
            EventContractor.postBoxConditionUpdate(blockType = BlockType.ROULETTE)
        } else {
            isVisible = false
        }
    }

    init {
        if (SettingContractor.appInfoSetting.allowTicketBox) {
            isVisible = TicketBoxController.hasTicketBox
            vb.ticketBoxActionConfirm.setOnClickWithDebounce {
                ownerActivity?.let {
                    when (NotificationController.Host.useHostNotification) {
                        true -> NotificationController.Host.showTicketBoxBannerDialog(activity = it)
                        false -> NotificationController.SDK.showTicketBoxBannerDialog(activity = it)
                    }
                }
            }
        } else {
            isVisible = false
        }
    }
}