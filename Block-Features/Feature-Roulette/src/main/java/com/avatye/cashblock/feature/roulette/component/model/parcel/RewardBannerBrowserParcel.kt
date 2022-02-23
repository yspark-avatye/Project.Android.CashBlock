package com.avatye.cashblock.feature.roulette.component.model.parcel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class RewardBannerBrowserParcel(
    val clickID: String,
    val landingUrl: String
) : Parcelable {
    companion object {
        const val NAME = "parcel:reward-banner-browser"
    }
}