package com.avatye.cashblock.base.presentation.parcel

import android.os.Parcelable
import com.avatye.cashblock.base.block.BlockType
import kotlinx.parcelize.Parcelize
import org.joda.time.DateTime

@Parcelize
internal data class InspectionParcel(
    val blockId: String,
    val blockSecret: String,
    val blockType: BlockType,
    val startDateTime: DateTime? = null,
    val endDateTime: DateTime? = null,
    val message: String,
    val link: String
) : Parcelable {
    companion object {
        const val NAME = "parcel:inspection"
    }
}