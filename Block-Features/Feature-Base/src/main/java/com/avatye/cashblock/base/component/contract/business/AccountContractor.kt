package com.avatye.cashblock.base.component.contract.business

import com.avatye.cashblock.base.Core
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.user.AgeVerifiedType
import com.avatye.cashblock.base.component.domain.listener.ILoginListener
import com.avatye.cashblock.base.internal.controller.LoginController
import com.avatye.cashblock.base.internal.preference.AccountPreferenceData

object AccountContractor {

    private const val tagName = "AccountContractor"

    // region { Account Setting }
    val isLogin: Boolean
        get() = Core.isInitialized && AccountPreferenceData.isValid

    val appUserID: String
        get() = AccountPreferenceData.appUserId

    val sdkUserID: String
        get() = AccountPreferenceData.sdkUserId

    val ageVerified: AgeVerifiedType
        get() {
            return when (SettingContractor.appInfoSetting.allowAgeVerification) {
                true -> AccountPreferenceData.ageVerified
                false -> AgeVerifiedType.VERIFIED
            }
        }

    fun login(blockType: BlockType, callback: (success: Boolean) -> Unit) {
        LoginController.requestLogin(blockType = blockType, listener = object : ILoginListener {
            override fun onSuccess() = callback(true)
            override fun onFailure(reason: String) = callback(false)
        })
    }
    // endregion

    // login controller
    private object Controller {
        /*
        // region { account fields }
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
            if (LoginController.isLogin) {
                listener.onSuccess()
                return
            }

            UserApiContractor(blockType = blockType).let { contract ->
                // login -> response
                contract.postLogin(appUserId = loginAppUserID) {
                    when (it) {
                        is ContractResult.Success -> {
                            Core.logger.i(viewName = tagName) { "requestLogin -> onSuccess -> ${it.contract}" }
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
                            Core.logger.e(viewName = LoginController.tagName) { "requestLogin -> onFailure -> $it" }
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
         */
    }
}