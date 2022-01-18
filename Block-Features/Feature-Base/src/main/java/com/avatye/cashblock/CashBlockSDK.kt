package com.avatye.cashblock

import android.app.Application
import androidx.annotation.Keep
import com.avatye.cashblock.base.Core
import com.avatye.cashblock.base.component.contract.business.AccountContractor
import com.avatye.cashblock.base.component.domain.entity.user.Profile

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
    fun setUserProfile(profile: Profile) = AccountContractor.setUserProfile(profile = profile)

    @JvmStatic
    fun getUserProfile() = AccountContractor.userProfile

    @JvmStatic
    fun setCustomData(customData: String?) {
        Core.appCustomData = customData
    }
}