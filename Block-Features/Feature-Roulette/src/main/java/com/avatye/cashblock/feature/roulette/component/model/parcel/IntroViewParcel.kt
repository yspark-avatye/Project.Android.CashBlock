package com.avatye.cashblock.feature.roulette.component.model.parcel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class IntroViewParcel(val source: Int) : Parcelable {
    companion object {
        const val NAME = "parcel:intro-view"
    }
}