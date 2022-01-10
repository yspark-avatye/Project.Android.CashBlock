package com.avatye.cashblock.base.library.ad.curator.linear.loader

import android.view.View

internal interface ILinearADCallback {
    fun onLoadSuccess(view: View)
    fun onLoadFailed(isBlocked: Boolean)
}