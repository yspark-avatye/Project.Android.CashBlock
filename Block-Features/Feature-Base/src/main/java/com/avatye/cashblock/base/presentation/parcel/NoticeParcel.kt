package com.avatye.cashblock.base.presentation.parcel

import android.os.Parcelable
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.widget.header.HeaderView
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class NoticeParcel(
    val blockId: String,
    val blockSecret: String,
    val blockType: BlockType,
    val headerType: HeaderView.HeaderType,
    val noticeId: String? = null
) : Parcelable {
    companion object {
        const val NAME = "parcel:notice"
    }

    val isValid: Boolean
        get() {
            return blockId.isNotEmpty()
                    && blockSecret.isNotEmpty()
        }
}