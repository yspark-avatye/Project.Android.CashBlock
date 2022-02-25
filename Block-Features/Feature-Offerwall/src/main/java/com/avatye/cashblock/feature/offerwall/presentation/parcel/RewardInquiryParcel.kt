package com.avatye.cashblock.feature.offerwall.presentation.parcel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class RewardInquiryParcel(
    val advertiseID: String, val contactID: String? = null, val title: String, val state: Int = 0
) : Parcelable {
    internal companion object {
        const val NAME: String = "parcel:reward-inquiry"
    }
}