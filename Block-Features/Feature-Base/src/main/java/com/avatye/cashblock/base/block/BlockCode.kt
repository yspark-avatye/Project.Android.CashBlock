package com.avatye.cashblock.base.block

import com.avatye.cashblock.base.component.support.toBase64

data class BlockCode(val blockType: BlockType, val blockId: String, val blockSecret: String) {

    val basicToken: String get() = "$blockId:$blockSecret".toBase64

    override fun toString(): String {
        return "$blockId:$blockSecret"
    }

    companion object {
        fun create(blockType: BlockType, appKey: String): BlockCode {
            val value = appKey.split(":").filter { it != "" }
            return if (value.size == 2) {
                BlockCode(blockType = blockType, blockId = value[0], blockSecret = value[1])
            } else {
                BlockCode(blockType = blockType, blockId = "", blockSecret = "")
            }
        }
    }
}