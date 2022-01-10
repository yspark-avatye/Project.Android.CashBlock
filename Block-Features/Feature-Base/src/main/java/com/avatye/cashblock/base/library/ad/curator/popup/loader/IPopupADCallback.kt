package com.avatye.cashblock.base.library.ad.curator.popup.loader

import android.view.View

internal interface IPopupADCallback {
    fun onLoadSuccess(view: View, currentNetwork: Int)
    fun onLoadFailed(isBlocked: Boolean)
}