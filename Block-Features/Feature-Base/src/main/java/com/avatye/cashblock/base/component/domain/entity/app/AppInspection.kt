package com.avatye.cashblock.base.component.domain.entity.app

import com.avatye.cashblock.base.block.BlockType
import org.joda.time.DateTime

data class AppInspection(
    val blockType: BlockType,
    val fromDateTime: DateTime? = null,
    val toDateTime: DateTime? = null,
    val message: String,
    val link: String
)