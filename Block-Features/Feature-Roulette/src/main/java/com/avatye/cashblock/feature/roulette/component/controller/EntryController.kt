package com.avatye.cashblock.feature.roulette.component.controller

import android.content.Context
import com.avatye.cashblock.base.block.BlockController
import com.avatye.cashblock.base.component.contract.business.CoreContractor
import com.avatye.cashblock.base.component.domain.entity.base.EntrySourceType
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.RouletteConfig.logger
import com.avatye.cashblock.feature.roulette.presentation.view.intro.IntroActivity

internal object EntryController {

    private const val viewName = "EntryController"

    fun connect(context: Context) {
        // check initialize
        if (!CoreContractor.isInitialized) {
            logger.p(viewName = viewName) { "open { Core Context is not initialized, please check your Application Class }" }
            return
        }
        // already synced by BlockController
        logger.i(viewName = viewName) { "connect()" }
        IntroActivity.open(context = context, entrySource = EntrySourceType.DIRECT)
    }

    fun open(context: Context) {
        // check initialize
        if (!CoreContractor.isInitialized) {
            logger.p(viewName = viewName) { "open { Core Context is not initialized, please check your Application Class }" }
            return
        }
        // need sync
        BlockController.syncBlockSession(blockType = RouletteConfig.blockType) { success ->
            logger.i(viewName = viewName) { "open -> BlockController.syncBlockSession { success: $success }" }
            if (success) {
                IntroActivity.open(context = context, entrySource = EntrySourceType.DIRECT)
            }
        }
    }
}