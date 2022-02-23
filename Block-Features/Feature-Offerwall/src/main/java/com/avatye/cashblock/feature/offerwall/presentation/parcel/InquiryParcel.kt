package com.avatye.cashblock.feature.offerwall.presentation.parcel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class InquiryParcel(
    val advertiseId: String,
    val contactId: String? = null,
    val title: String,
    val state: Int = 0
) : Parcelable {
    internal companion object {
        const val NAME: String = "parcel:offerwall-inquiry"
    }
}