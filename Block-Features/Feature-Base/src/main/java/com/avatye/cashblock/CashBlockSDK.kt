package com.avatye.cashblock

import android.app.Application
import androidx.annotation.Keep
import com.avatye.cashblock.base.FeatureCore
import com.avatye.cashblock.base.block.BlockCode

@Keep
object CashBlockSDK {
    @JvmStatic
    fun initialize(application: Application) {
        FeatureCore.initialize(application = application)
    }

    fun initialize(application: Application, blockCode: BlockCode) {
        FeatureCore.initialize(application = application, blockCode = blockCode)
    }

    @JvmStatic
    fun setAppUserId(appUserId: String) {
        FeatureCore.setAppUserId(appUserId = appUserId)
    }

    @JvmStatic
    fun getAppUserId(): String {
        return FeatureCore.getAppUserId()
    }

    @JvmStatic
    fun setCustomData(customData: String?) {
        FeatureCore.appCustomData = customData
    }
}