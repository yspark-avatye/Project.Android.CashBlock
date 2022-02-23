package com.avatye.sdk.cashbutton.core.widget.stickylayout

import android.view.View

interface StickyHeaders {
    fun isStickyHeader(position: Int): Boolean

    interface ViewSetup {
        fun setupStickyHeaderView(header: View)
        fun teardownStickyHeaderView(header: View)
    }
}