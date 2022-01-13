package com.avatye.cashblock.feature.offerwall

import android.app.Application
import android.content.Context
import androidx.annotation.Keep
import com.avatye.cashblock.base.block.BlockController
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.business.CoreContractor
import com.avatye.cashblock.base.component.domain.listener.IPopupNoticeDataListener
import com.avatye.cashblock.base.internal.controller.PopupNoticeController
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.feature.offerwall.presentation.view.intro.IntroActivity


internal const val MODULE_NAME = "Offerwall@Block"

@Keep
internal object OfferwallConfig {
    // logger
    val logger: LogHandler = LogHandler(moduleName = MODULE_NAME)

    // region # base config
    val application: Application
        get() {
            return CoreContractor.coreContext as Application
        }

    val blockType: BlockType = BlockType.OFFERWALL

    val popupNoticeController: PopupNoticeController by lazy {
        PopupNoticeController(blockType = blockType, popupNoticeDataListener = object : IPopupNoticeDataListener {
            override fun setItems(data: Map<String, Int>) {
                //TODO("Not yet implemented")
            }

            override fun getItems(): LinkedHashMap<String, Int> {
                return super.getItems()
            }
        })
    }
    // endregion

    fun initialize(callback: (success: Boolean) -> Unit) {
        callback(true)
    }

    fun openFromConnector(context: Context) {
        if (CoreContractor.isInitialized) {
            logger.i(viewName = "Config") { "## CashBlock -> Offerwall -> openFromConnector(${blockType.name})" }
            OfferwallConfig.initialize { isInit ->
                when (isInit) {
                    true -> IntroActivity.open(context = context)
                    false -> logger.i { "## CashBlock -> Offerwall { initialize failed }" }
                }
            }
        } else {
            logger.i(viewName = "Config") { "## CashBlock -> Offerwall { Core Context is not initialized, please check your Application Class }" }
        }
    }

    fun open(context: Context) {
        if (CoreContractor.isInitialized) {
            BlockController.syncBlockSession(blockType = blockType) { success ->
                if (success) {
                    logger.i(viewName = "Config") { "## CashBlock -> Offerwall -> syncBlockSession -> open -> success" }
                    initialize { isInit ->
                        if (isInit) {
                            IntroActivity.open(context = context)
                        } else {
                            logger.i(viewName = "Config") { "## CashBlock -> Offerwall { initialize failed }" }
                        }
                    }
                } else {
                    logger.i(viewName = "Config") { "## CashBlock -> Offerwall { syncBlockSession failed }" }
                }
            }
        } else {
            logger.i(viewName = "Config") { "## CashBlock -> Roulette { Core Context is not initialized, please check your Application Class }" }
        }
    }
}