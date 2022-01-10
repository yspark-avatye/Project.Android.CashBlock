package com.avatye.cashblock.base.library.ad.curator.linear

import android.view.View

interface ICuratorLinearCallback {
    fun oSuccess(adView: View)
    fun onFailure(isBlocked: Boolean)
    fun onNeedAgeVerification()
}