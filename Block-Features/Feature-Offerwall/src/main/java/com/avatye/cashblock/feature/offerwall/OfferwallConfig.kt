package com.avatye.cashblock.feature.offerwall

import android.app.Application
import android.content.Context
import androidx.annotation.Keep
import com.avatye.cashblock.base.FeatureCore
import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.block.BlockController
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.CoreContract
import com.avatye.cashblock.base.component.support.metaDataValue
import com.avatye.cashblock.base.internal.controller.popupNotice.IPopupNoticeDataStore
import com.avatye.cashblock.base.internal.controller.popupNotice.PopupNoticeController
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.feature.offerwall.component.data.PreferenceData
import com.avatye.cashblock.feature.offerwall.presentation.view.intro.IntroActivity


internal const val MODULE_NAME = "Offerwall@Feature"

@Keep
internal object OfferwallConfig {
    // logger
    val logger: LogHandler = LogHandler(moduleName = MODULE_NAME)

    // region # base config
    val application: Application
        get() {
            return CoreContract.coreContext as Application
        }

    lateinit var blockCode: BlockCode
        private set

    lateinit var popupNoticeController: PopupNoticeController
        private set
    // endregion

    fun initialize(blockCode: BlockCode? = null, callback: (success: Boolean) -> Unit) {
        if (blockCode != null) {
            // init block code
            this.blockCode = blockCode
            // init notice controller
            this.popupNoticeController = PopupNoticeController(
                blockCode = this.blockCode,
                popupNoticeDataStore = object : IPopupNoticeDataStore {
                    override fun getItems(): LinkedHashMap<String, Int> = PreferenceData.PopupNotice.popupCloseDate
                    override fun setItems(data: Map<String, Int>) = PreferenceData.PopupNotice.update(popupCloseDate = data)
                })
            // callback
            callback(true)
        } else {
            // from meta data
            this.application.metaDataValue(FeatureCore.CASHBLOCK_KEY_OFFERWALL).let {
                if (it.isNullOrEmpty()) {
                    // error callback
                    logger.e { "${FeatureCore.CASHBLOCK_KEY_OFFERWALL} is null or empty" }
                    callback(false)
                } else {
                    // init block code
                    this.blockCode = BlockCode.create(blockType = BlockType.OFFERWALL, appKey = it)
                    // init notice controller
                    this.popupNoticeController = PopupNoticeController(
                        blockCode = this.blockCode,
                        popupNoticeDataStore = object : IPopupNoticeDataStore {
                            override fun getItems(): LinkedHashMap<String, Int> = PreferenceData.PopupNotice.popupCloseDate
                            override fun setItems(data: Map<String, Int>) = PreferenceData.PopupNotice.update(popupCloseDate = data)
                        })
                    // callback
                    callback(true)
                }
            }
        }
    }

    fun openFromConnector(context: Context, blockCode: BlockCode?) {
        if (CoreContract.isInitialized) {
            logger.i { "## CashBlock -> Offerwall -> openFromConnector(${blockCode})" }
            OfferwallConfig.initialize(blockCode = blockCode) { isInit ->
                when (isInit) {
                    true -> IntroActivity.open(context = context)
                    false -> logger.i { "## CashBlock -> Offerwall { initialize failed }" }
                }
            }
        } else {
            logger.i { "## CashBlock -> Offerwall { Core Context is not initialized, please check your Application Class }" }
        }
    }

    fun open(context: Context, blockCode: BlockCode? = null) {
        if (CoreContract.isInitialized) {
            BlockController.syncBlockSession { success ->
                if (success) {
                    logger.i { "## CashBlock -> Offerwall -> syncBlockSession -> open -> success" }
                    initialize(blockCode = blockCode) { isInit ->
                        if (isInit) {
                            IntroActivity.open(context = context)
                        } else {
                            logger.i { "## CashBlock -> Offerwall { initialize failed }" }
                        }
                    }
                } else {
                    logger.i { "## CashBlock -> Offerwall { syncBlockSession failed }" }
                }
            }
        } else {
            logger.i { "## CashBlock -> Roulette { Core Context is not initialized, please check your Application Class }" }
        }
    }
}