package com.avatye.cashblock.base.component.contract

import com.avatye.cashblock.base.component.domain.entity.user.AgeVerifiedType
import com.avatye.cashblock.base.internal.controller.LoginController
import com.avatye.cashblock.base.internal.preference.AccountPreferenceData

object AccountContract {
    // region { Account Setting }
    val isLogin: Boolean
        get() = LoginController.isLogin

    val appUserID: String
        get() = AccountPreferenceData.appUserId

    val sdkUserID: String
        get() = AccountPreferenceData.sdkUserId

    val ageVerified: AgeVerifiedType
        get() {
            return when (RemoteContract.appInfoSetting.allowAgeVerification) {
                true -> AccountPreferenceData.ageVerified
                false -> AgeVerifiedType.VERIFIED
            }
        }

    val accessToken: String
        get() = AccountPreferenceData.accessToken

    fun login(callback: (success: Boolean) -> Unit) {
        LoginController.requestLogin(listener = object : LoginController.ILoginListener {
            override fun onSuccess() = callback(true)
            override fun onFailure(reason: String) = callback(false)
        })
    }
    // endregion
}