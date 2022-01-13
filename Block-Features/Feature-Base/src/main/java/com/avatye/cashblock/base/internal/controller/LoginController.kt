package com.avatye.cashblock.base.internal.controller

import com.avatye.cashblock.R
import com.avatye.cashblock.base.Core
import com.avatye.cashblock.base.Core.logger
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.api.UserApiContractor
import com.avatye.cashblock.base.component.contract.business.SettingContractor
import com.avatye.cashblock.base.component.domain.listener.ILoginListener
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.internal.preference.AccountPreferenceData

internal object LoginController {

    private const val tagName = "LoginController"

    // region { account fields }
    val isLogin: Boolean
        get() {
            return Core.isInitialized && AccountPreferenceData.isValid
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
    internal fun requestLogin(blockType: BlockType, listener: ILoginListener) {
        // valid init
        if (!Core.isInitialized) {
            listener.onFailure(reason = Core.application.getString(R.string.acb_common_message_error))
            return
        }

        // valid appUserID
        val loginAppUserID = AccountPreferenceData.appUserId
        if (loginAppUserID.isEmpty()) {
            listener.onFailure(reason = Core.application.getString(R.string.acb_common_message_profile_not_set))
            return
        }

        // send 'onSuccess' callback when already login
        if (isLogin) {
            listener.onSuccess()
            return
        }

        UserApiContractor(blockType = blockType).let { contract ->
            // login -> response
            contract.postLogin(appUserId = loginAppUserID) {
                when (it) {
                    is ContractResult.Success -> {
                        logger.i(viewName = tagName) { "requestLogin -> onSuccess -> ${it.contract}" }
                        // core - clear session
                        AccountPreferenceData.clearSession()
                        AccountPreferenceData.update(
                            sdkUserId = it.contract.sdkUserID,
                            accessToken = it.contract.accessToken,
                            ageVerifiedType = it.contract.ageVerifiedType
                        )
                        // app setting sync
                        when (SettingContractor.Controller.isSynced) {
                            true -> listener.onSuccess()
                            false -> SettingContractor.Controller.synchronization(blockType = blockType) {
                                listener.onSuccess()
                            }
                        }
                    }
                    is ContractResult.Failure -> {
                        logger.e(viewName = tagName) { "requestLogin -> onFailure -> $it" }
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