package com.avatye.cashblock.base.presentation

import com.avatye.cashblock.base.FeatureCore
import com.avatye.cashblock.base.MODULE_NAME
import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.component.domain.model.app.CoreBaseActivity


internal abstract class AppBaseActivity : CoreBaseActivity() {
    override val blockCode: BlockCode
        get() = FeatureCore.coreBlockCode

    override val blockName: String
        get() = MODULE_NAME
}