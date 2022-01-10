package com.avatye.cashblock.feature.roulette

import android.app.Application
import android.content.Context
import androidx.annotation.Keep
import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.block.BlockController
import com.avatye.cashblock.base.component.contract.CoreContract
import com.avatye.cashblock.base.internal.controller.popupNotice.IPopupNoticeDataStore
import com.avatye.cashblock.base.internal.controller.popupNotice.PopupNoticeController
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.feature.roulette.component.data.PreferenceData
import com.avatye.cashblock.feature.roulette.component.model.entity.notification.NotificationServiceConfig
import com.avatye.cashblock.feature.roulette.presentation.view.intro.IntroActivity


internal const val MODULE_NAME = "Roulette@Feature"

@Keep
internal object RouletteConfig {
    // logger
    val logger: LogHandler = LogHandler(moduleName = MODULE_NAME)

    // region # base config
    val application: Application
        get() {
            return CoreContract.coreContext as Application
        }

    val blockCode: BlockCode
        get() {
            return CoreContract.coreBlockCode
        }

    var notificationServiceConfig: NotificationServiceConfig = NotificationServiceConfig()

    val popupNoticeController: PopupNoticeController by lazy {
        PopupNoticeController(blockCode = this.blockCode, popupNoticeDataStore = object : IPopupNoticeDataStore {
            override fun getItems(): LinkedHashMap<String, Int> {
                return PreferenceData.PopupNotice.popupCloseDate
            }

            override fun setItems(data: Map<String, Int>) {
                PreferenceData.PopupNotice.update(popupCloseDate = data)
            }
        })
    }
    // endregion


    fun openFromConnector(context: Context) {
        if (CoreContract.isInitialized) {
            logger.i { "## CashBlock -> Roulette -> openFromConnector(${RouletteConfig.blockCode})" }
            IntroActivity.open(context = context)
        } else {
            logger.i { "## CashBlock -> Roulette { Core Context is not initialized, please check your Application Class }" }
        }
    }

    fun open(context: Context) {
        if (CoreContract.isInitialized) {
            BlockController.syncBlockSession {
                if (it) {
                    logger.i { "## CashBlock -> Roulette -> syncBlockSession -> open -> success" }
                    IntroActivity.open(context = context)
                } else {
                    logger.i { "## CashBlock -> Roulette { syncBlockSession failed }" }
                }
            }
        } else {
            logger.i { "## CashBlock -> Roulette { Core Context is not initialized, please check your Application Class }" }
        }
    }
}