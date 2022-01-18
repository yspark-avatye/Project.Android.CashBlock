package com.avatye.cashblock.feature.roulette

import android.app.Application
import android.content.Context
import androidx.annotation.Keep
import com.avatye.cashblock.base.block.BlockController
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.business.CoreContractor
import com.avatye.cashblock.base.component.contract.business.PopupNoticeContractor
import com.avatye.cashblock.base.component.domain.listener.IPopupNoticeDataListener
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.feature.roulette.component.data.PreferenceData
import com.avatye.cashblock.feature.roulette.component.model.entity.notification.NotificationServiceConfig
import com.avatye.cashblock.feature.roulette.presentation.view.intro.IntroActivity


internal const val MODULE_NAME = "Roulette@Block"

@Keep
internal object RouletteConfig {
    // logger
    val logger: LogHandler = LogHandler(moduleName = MODULE_NAME)

    // region # base config
    val application: Application
        get() {
            return CoreContractor.coreContext as Application
        }

    val blockType: BlockType = BlockType.ROULETTE

    var notificationServiceConfig: NotificationServiceConfig = NotificationServiceConfig()

    val popupNoticeController: PopupNoticeContractor by lazy {
        PopupNoticeContractor(blockType = blockType, popupNoticeDataListener = object : IPopupNoticeDataListener {
            override fun getItems(): LinkedHashMap<String, Int> {
                return PreferenceData.PopupNotice.popupCloseDate
            }

            override fun setItems(data: Map<String, Int>) {
                PreferenceData.PopupNotice.update(popupCloseDate = data)
            }
        })
    }
    // endregion
}