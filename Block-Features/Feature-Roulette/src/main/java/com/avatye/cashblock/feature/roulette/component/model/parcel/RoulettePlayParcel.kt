package com.avatye.cashblock.feature.roulette.component.model.parcel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class RoulettePlayParcel(
    val rouletteID: String,
    val rouletteName: String,
    val useTicketAmount: Int,
    val iconUrl: String,
    val imageUrl: String
) : Parcelable {
    companion object {
        const val NAME = "parcel:roulette-play"
    }
}