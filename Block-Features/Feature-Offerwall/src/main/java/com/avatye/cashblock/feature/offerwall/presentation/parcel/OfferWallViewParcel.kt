package com.avatye.cashblock.feature.offerwall.presentation.parcel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class OfferWallViewParcel(
    val advertiseID: String
    , val currentPos: Int
    , val title: String
    , val reward: Int
) : Parcelable {
    internal companion object {
        const val NAME: String = "parcel:offerwall-view"
    }
}