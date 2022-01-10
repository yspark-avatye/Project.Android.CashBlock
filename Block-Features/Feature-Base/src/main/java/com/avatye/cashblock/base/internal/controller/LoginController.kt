package com.avatye.cashblock.base.internal.controller

import com.avatye.cashblock.base.FeatureCore
import com.avatye.cashblock.base.MODULE_NAME
import com.avatye.cashblock.R
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.base.internal.preference.AccountPreferenceData
import com.avatye.cashblock.base.component.contract.data.UserDataContract

internal object LoginController {

    private const val tagName = "LoginController"

    // region { interface }
    interface ILoginListener {
        fun onSuccess()
        fun onFailure(reason: String)
    }
    // endregion

    // region { account fields }
    val isLogin: Boolean
        get() {
            return FeatureCore.isInitialized && AccountPreferenceData.isValid
        }

    fun setLoginUserId(appUserId: String) {
        if (isLogin) {
            if (AccountPreferenceData.needUpdateAppUserId(targetAppUserId = appUserId)) {
                AccountPreferenceData.clearSession(appUserId = appUserId)
            }
        } else {
            AccountPreferenceData.update(appUserId = appUserId)
        }
    }

    fun getLoginUserId() = AccountPreferenceData.appUserId
    // endregion

    // region { login - logout }
    internal fun requestLogin(listener: ILoginListener) {
        // valid init
        if (!FeatureCore.isInitialized) {
            listener.onFailure(reason = FeatureCore.application.getString(R.string.acb_common_message_error))
            return
        }

        // valid appUserID
        val loginAppUserID = AccountPreferenceData.appUserId
        if (loginAppUserID.isEmpty()) {
            listener.onFailure(reason = FeatureCore.application.getString(R.string.acb_common_message_profile_not_set))
            return
        }

        // send 'onSuccess' callback when already login
        if (isLogin) {
            listener.onSuccess()
            return
        }

        UserDataContract(FeatureCore.coreBlockCode).let { contract ->
            // login -> response
            contract.postLogin(appUserId = loginAppUserID) {
                when (it) {
                    is ContractResult.Success -> {
                        LogHandler.i(moduleName = MODULE_NAME) {
                            "$tagName -> requestLogin -> onSuccess -> ${it.contract}"
                        }
                        // core - clear session
                        AccountPreferenceData.clearSession()
                        AccountPreferenceData.update(
                            sdkUserId = it.contract.sdkUserID,
                            accessToken = it.contract.accessToken,
                            ageVerifiedType = it.contract.ageVerifiedType
                        )
                        // app setting sync
                        when (RemoteSettingSyncController.isSynced) {
                            true -> listener.onSuccess()
                            false -> RemoteSettingSyncController.synchronization {
                                listener.onSuccess()
                            }
                        }
                    }
                    is ContractResult.Failure -> {
                        LogHandler.e(moduleName = MODULE_NAME) {
                            "$tagName -> requestLogin -> onFailure -> $it"
                        }
                        listener.onFailure(reason = it.message)
                    }
                }
            }
        }
    }

    internal fun requestLogout() {
        AccountPreferenceData.clearSession()
    }
    // endregion
}