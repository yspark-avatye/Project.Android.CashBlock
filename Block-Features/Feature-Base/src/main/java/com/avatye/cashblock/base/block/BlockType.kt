package com.avatye.cashblock.base.block

import androidx.annotation.Keep
import com.avatye.cashblock.base.FeatureCore

@Keep
enum class BlockType(val value: String) {
    ROULETTE(FeatureCore.CASHBLOCK_CONNECT_ROULETTE),
    OFFERWALL(FeatureCore.CASHBLOCK_CONNECT_OFFERWALL),
    PUBLISHER(FeatureCore.CASHBLOCK_CONNECT_PUBLISHER);
}


