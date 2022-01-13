package com.avatye.cashblock.base.block

import androidx.annotation.Keep
import com.avatye.cashblock.base.CoreConstants

@Keep
enum class BlockType(val value: Int) {
    CORE(1),
    ROULETTE(100),
    OFFERWALL(101),
    PUBLISHER(900);

    companion object {
        fun from(value: Int): BlockType? {
            return when (value) {
                1 -> CORE
                100 -> ROULETTE
                101 -> OFFERWALL
                900 -> PUBLISHER
                else -> null
            }
        }

        val BlockType.connector: String
            get() {
                return when (this) {
                    CORE -> ""
                    ROULETTE -> CoreConstants.CASHBLOCK_CONNECT_ROULETTE
                    OFFERWALL -> CoreConstants.CASHBLOCK_CONNECT_OFFERWALL
                    PUBLISHER -> CoreConstants.CASHBLOCK_CONNECT_PUBLISHER
                }
            }
    }
}