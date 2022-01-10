package com.avatye.cashblock.base.library.ad.curator.popup

import android.view.View
import com.avatye.cashblock.base.library.ad.curator.ADNetworkType

interface ICuratorPopupCallback {
    fun oSuccess(adView: View, network: ADNetworkType)
    fun onFailure(isBlocked: Boolean)
    fun onNeedAgeVerification()
}