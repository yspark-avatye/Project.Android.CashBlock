package com.avatye.cashblock.base.presentation.parcel

import android.os.Parcelable
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.widget.header.HeaderView
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class TermsParcel(
    val blockType: BlockType,
    val headerType: HeaderView.HeaderType = HeaderView.HeaderType.POPUP,
    val url: String = ""
) : Parcelable {
    companion object {
        const val NAME = "parcel:terms"
    }
}