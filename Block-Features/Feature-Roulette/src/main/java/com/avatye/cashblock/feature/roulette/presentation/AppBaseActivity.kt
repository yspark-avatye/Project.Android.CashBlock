package com.avatye.cashblock.feature.roulette.presentation

import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.model.app.CoreBaseActivity
import com.avatye.cashblock.feature.roulette.RouletteConfig


internal abstract class AppBaseActivity : CoreBaseActivity() {
    override val blockType: BlockType = RouletteConfig.blockType
}