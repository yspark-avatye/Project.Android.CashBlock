package com.avatye.cashblock.feature.offerwall.presentation.parcel

import android.os.Parcelable
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallJourneyStateType
import kotlinx.android.parcel.Parcelize


@Parcelize
internal data class OfferWallActionParcel(
    val currentPosition: Int = 0,
    val journeyType: OfferwallJourneyStateType,
    val forceRefresh: Boolean = false
) : Parcelable {
    internal companion object {
        const val NAME: String = "parcel:offerwall-action-result"
    }
}