package com.avatye.cashblock.feature.offerwall

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.annotation.Keep
import com.avatye.cashblock.base.block.BlockController
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.business.CoreContractor
import com.avatye.cashblock.base.component.contract.business.PopupNoticeContractor
import com.avatye.cashblock.base.component.domain.listener.IPopupNoticeDataListener
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.feature.offerwall.presentation.view.intro.IntroActivity
import com.avatye.cashblock.feature.offerwall.presentation.view.main.OfferwallMainActivity


internal const val MODULE_NAME = "Offerwall@Block"

@Keep
internal object OfferwallConfig {
    // logger
    private const val viewName: String = "OfferwallConfig"
    val logger: LogHandler = LogHandler(moduleName = MODULE_NAME)

    // region # base config
    val application: Application
        get() {
            return CoreContractor.coreContext as Application
        }

    val blockType: BlockType = BlockType.OFFERWALL

    val popupNoticeController: PopupNoticeContractor by lazy {
        PopupNoticeContractor(blockType = blockType, popupNoticeDataListener = object : IPopupNoticeDataListener {
            override fun setItems(data: Map<String, Int>) {
                //TODO("Not yet implemented")
            }

            override fun getItems(): LinkedHashMap<String, Int> {
                return super.getItems()
            }
        })
    }
    // endregion
}