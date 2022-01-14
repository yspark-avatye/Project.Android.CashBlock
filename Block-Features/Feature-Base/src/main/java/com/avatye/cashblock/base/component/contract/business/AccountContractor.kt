package com.avatye.cashblock.base.component.contract.business

import com.avatye.cashblock.R
import com.avatye.cashblock.base.Core
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.api.UserApiContractor
import com.avatye.cashblock.base.component.domain.entity.user.AgeVerifiedType
import com.avatye.cashblock.base.component.domain.entity.user.Profile
import com.avatye.cashblock.base.component.domain.listener.ILoginListener
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.internal.preference.AccountPreferenceData

object AccountContractor {

    private const val tagName = "AccountContractor"

    val isLogin: Boolean get() = Controller.isLogin

    val userProfile: Profile get() = Controller.userProfile

    val sdkUserId: String get() = Controller.sdkUserId

    val ageVerified: AgeVerifiedType get() = Controller.ageVerified

    fun setUserProfile(profile: Profile) = Controller.setUserProfile(profile = profile)

    fun login(blockType: BlockType, listener: ILoginListener) = Controller.requestLogin(blockType = blockType, listener = listener)

    fun logout() = Controller.requestLogout()

    private object Controller {
        val isLogin get() = Core.isInitialized && AccountPreferenceData.isValid
        val userProfile get() = AccountPreferenceData.profile
        val sdkUserId get() = AccountPreferenceData.sdkUserId
        val ageVerified: AgeVerifiedType
            get() {
                return when (SettingContractor.appInfoSetting.allowAgeVerification) {
                    true -> AccountPreferenceData.ageVerified
                    false -> AgeVerifiedType.VERIFIED
                }
            }

        fun setUserProfile(profile: Profile) {
            if (isLogin) {
                if (AccountPreferenceData.needUpdateAppUserProfile(profile = profile)) {
                    AccountPreferenceData.clearSession(profile = profile)
                }
            } else {
                AccountPreferenceData.update(profile = profile)
            }
        }

        fun requestLogin(blockType: BlockType, listener: ILoginListener) {
            // valid init
            if (!Core.isInitialized) {
                listener.onFailure(reason = Core.application.getString(R.string.acb_common_message_error))
                return
            }

            // valid appUserID
            val loginProfile = AccountPreferenceData.profile
            if (!loginProfile.isValid()) {
                AccountPreferenceData.clearProfile()
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
                contract.postLogin(profile = loginProfile) {
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
                            Core.logger.e(viewName = tagName) { "requestLogin -> onFailure -> $it" }
                            listener.onFailure(reason = it.message)
                        }
                    }
                }
            }
        }

        fun requestLogout() {
            AccountPreferenceData.clearSession()
        }
    }
}