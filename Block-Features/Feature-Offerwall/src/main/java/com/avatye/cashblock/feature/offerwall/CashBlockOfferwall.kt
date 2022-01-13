package com.avatye.cashblock.feature.offerwall

import android.content.Context
import androidx.annotation.Keep

@Keep
object CashBlockOfferwall {

    internal fun openFromConnector(context: Context) {
        OfferwallConfig.openFromConnector(context = context)
    }

    @JvmStatic
    fun open(context: Context) {
        OfferwallConfig.open(context = context)
    }
}