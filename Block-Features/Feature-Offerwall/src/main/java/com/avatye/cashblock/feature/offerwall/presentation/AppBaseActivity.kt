package com.avatye.cashblock.feature.offerwall.presentation

import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.component.domain.model.app.CoreBaseActivity
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.feature.offerwall.OfferwallConfig
import com.avatye.cashblock.feature.offerwall.MODULE_NAME

internal abstract class AppBaseActivity : CoreBaseActivity() {
    protected val logger: LogHandler = LogHandler(MODULE_NAME)

    override val blockCode: BlockCode
        get() = OfferwallConfig.blockCode

    override val blockName: String
        get() = MODULE_NAME
}