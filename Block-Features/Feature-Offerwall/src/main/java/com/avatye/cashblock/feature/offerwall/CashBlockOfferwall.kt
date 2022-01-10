package com.avatye.cashblock.feature.offerwall

import android.content.Context
import androidx.annotation.Keep
import com.avatye.cashblock.base.block.BlockCode

@Keep
object CashBlockOfferwall {

    internal fun openFromConnector(context: Context, blockCode: BlockCode?) {
        OfferwallConfig.openFromConnector(context = context, blockCode = blockCode)
    }

    @JvmStatic
    fun open(context: Context) {
        OfferwallConfig.open(context = context, blockCode = null)
    }

    @JvmStatic
    fun open(context: Context, blockCode: BlockCode) {
        OfferwallConfig.open(context = context, blockCode = blockCode)
    }
}