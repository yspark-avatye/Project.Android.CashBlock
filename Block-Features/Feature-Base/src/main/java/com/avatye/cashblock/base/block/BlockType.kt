package com.avatye.cashblock.base.block

import androidx.annotation.Keep
import com.avatye.cashblock.base.FeatureCore

@Keep
enum class BlockType(val value: Int) {
    ROULETTE(1),
    OFFERWALL(2),
    PUBLISHER(3);

    companion object {
        fun from(value: Int): BlockType? {
            return when (value) {
                1 -> ROULETTE
                2 -> OFFERWALL
                3 -> PUBLISHER
                else -> null
            }
        }

        val BlockType.connector: String
            get() {
                return when (this) {
                    ROULETTE -> FeatureCore.CASHBLOCK_CONNECT_ROULETTE
                    OFFERWALL -> FeatureCore.CASHBLOCK_CONNECT_OFFERWALL
                    PUBLISHER -> FeatureCore.CASHBLOCK_CONNECT_PUBLISHER
                }
            }
    }
}