package com.avatye.cashblock.base.presentation

import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.model.app.CoreBaseActivity


internal abstract class AppBaseActivity : CoreBaseActivity() {
    override val blockType: BlockType = BlockType.OFFERWALL
}