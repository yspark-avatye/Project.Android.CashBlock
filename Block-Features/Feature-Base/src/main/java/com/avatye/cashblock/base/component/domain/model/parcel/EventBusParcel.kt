package com.avatye.cashblock.base.component.domain.model.parcel

import android.os.Parcelable
import com.avatye.cashblock.base.block.BlockType
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventBusParcel(val blockType: BlockType) : Parcelable {
    companion object {
        const val NAME = "parcel:eventbus"
    }
}