package com.avatye.cashblock.feature.roulette.presentation

import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.component.domain.model.app.CoreBaseActivity
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.MODULE_NAME


internal abstract class AppBaseActivity : CoreBaseActivity() {
    override val blockCode: BlockCode
        get() = RouletteConfig.blockCode

    override val blockName: String
        get() = MODULE_NAME
}