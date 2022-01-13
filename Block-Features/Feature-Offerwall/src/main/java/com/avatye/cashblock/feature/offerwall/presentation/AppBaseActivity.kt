package com.avatye.cashblock.feature.offerwall.presentation

import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.model.app.CoreBaseActivity
import com.avatye.cashblock.feature.offerwall.OfferwallConfig

internal abstract class AppBaseActivity : CoreBaseActivity() {
    override val blockType: BlockType = OfferwallConfig.blockType
}