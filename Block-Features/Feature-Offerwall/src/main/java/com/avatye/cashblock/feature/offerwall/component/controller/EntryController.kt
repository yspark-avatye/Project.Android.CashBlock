package com.avatye.cashblock.feature.offerwall.component.controller

import android.content.Context
import android.content.Intent
import com.avatye.cashblock.base.block.BlockController
import com.avatye.cashblock.base.component.contract.business.CoreContractor
import com.avatye.cashblock.base.component.domain.entity.base.EntrySourceType
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.feature.offerwall.OfferwallConfig
import com.avatye.cashblock.feature.offerwall.OfferwallConfig.logger
import com.avatye.cashblock.feature.offerwall.presentation.view.intro.IntroActivity

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
        IntroActivity.open(
            context = context,
            serviceType = ServiceType.ROULETTE,
            entrySource = EntrySourceType.DIRECT
        )
    }

    fun open(context: Context) {
        // check initialize
        if (!CoreContractor.isInitialized) {
            logger.p(viewName = viewName) { "open { Core Context is not initialized, please check your Application Class }" }
            return
        }
        // need sync
        BlockController.syncBlockSession(blockType = OfferwallConfig.blockType) { success ->
            logger.i(viewName = viewName) { "open -> BlockController.syncBlockSession { success: $success }" }
            if (success) {
                IntroActivity.open(
                    context = context,
                    serviceType = ServiceType.OFFERWALL,
                    entrySource = EntrySourceType.DIRECT
                )
            }
        }
    }

    /**
     * custom view 설정을 위한 함수
     */
    fun launch(context: Context, entryIntent: Intent) {
        // check initialize
        if (!CoreContractor.isInitialized) {
            logger.p(viewName = viewName) { "open { Core Context is not initialized, please check your Application Class }" }
            return
        }
        // need sync
        BlockController.syncBlockSession(blockType = OfferwallConfig.blockType) { success ->
            logger.i(viewName = viewName) { "open -> BlockController.syncBlockSession { success: $success }" }
            if (success) {
                IntroActivity.open(
                    context = context,
                    serviceType = ServiceType.OFFERWALL,
                    entryIntent = entryIntent,
                    entrySource = EntrySourceType.DIRECT
                )
            }
        }
    }
}