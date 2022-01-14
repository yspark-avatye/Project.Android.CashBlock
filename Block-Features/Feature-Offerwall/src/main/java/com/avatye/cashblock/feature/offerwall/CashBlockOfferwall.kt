package com.avatye.cashblock.feature.offerwall

import android.content.Context
import android.content.Intent
import androidx.annotation.Keep
import com.avatye.cashblock.feature.offerwall.component.controller.EntryController

@Keep
object CashBlockOfferwall {
    // connector open // from roulette -> serviceType.ROULETTE
    internal fun connect(context: Context) = EntryController.connect(context = context)


    @JvmStatic
    fun open(context: Context) = EntryController.open(context = context)


    @JvmStatic
    fun open(context: Context, activityIntent: Intent) = EntryController.launch(context = context, entryIntent = activityIntent)
}