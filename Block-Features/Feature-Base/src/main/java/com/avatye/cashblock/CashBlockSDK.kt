package com.avatye.cashblock

import android.app.Application
import androidx.annotation.Keep
import com.avatye.cashblock.base.Core

@Keep
object CashBlockSDK {
    @JvmStatic
    fun initialize(application: Application) {
        Core.initialize(application = application)
    }

    fun initialize(application: Application, appId: String, appSecret: String) {
        Core.initialize(application = application, appId = appId, appSecret = appSecret)
    }

    @JvmStatic
    fun setAppUserId(appUserId: String) {
        Core.setAppUserId(appUserId = appUserId)
    }

    @JvmStatic
    fun getAppUserId(): String {
        return Core.getAppUserId()
    }

    @JvmStatic
    fun setCustomData(customData: String?) {
        Core.appCustomData = customData
    }
}