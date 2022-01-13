package com.avatye.cashblock.base.internal.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.avatye.cashblock.base.Core
import com.avatye.cashblock.base.block.BlockController
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.user.AgeVerifiedType

internal object AccountPreferenceData {
    private var _appUserId = Preference.appUserId
    private var _sdkUserId = Preference.sdkUserId
    private var _accessToken = Preference.accessToken
    private var _ageVerifiedType = Preference.ageVerifiedType

    val isValid: Boolean
        get() {
            return _appUserId.isNotEmpty()
                    && _sdkUserId.isNotEmpty()
                    && _accessToken.isNotEmpty()
        }

    val appUserId: String
        get() {
            return _appUserId
        }

    val sdkUserId: String
        get() {
            return _sdkUserId
        }

    val accessToken: String
        get() {
            return _accessToken
        }

    val ageVerified: AgeVerifiedType
        get() {
            return AgeVerifiedType.from(_ageVerifiedType)
        }

    fun needUpdateAppUserId(targetAppUserId: String): Boolean {
        return !targetAppUserId.equals(other = appUserId, ignoreCase = false)
    }

    fun update(appUserId: String? = null, sdkUserId: String? = null, accessToken: String? = null, ageVerifiedType: AgeVerifiedType? = null) {
        appUserId?.let {
            _appUserId = it
            Preference.appUserId = it
        }
        sdkUserId?.let {
            _sdkUserId = it
            Preference.sdkUserId = it
        }
        accessToken?.let {
            _accessToken = it
            Preference.accessToken = it
        }
        ageVerifiedType?.let {
            _ageVerifiedType = it.value
            Preference.ageVerifiedType = it.value
        }
    }

    fun clearSession(appUserId: String? = null, sdkUserId: String? = null, accessToken: String? = null, ageVerifiedType: AgeVerifiedType? = null) {
        // clear core session
        Preference.clear()
        // clear connector session
        if (BlockController.hasBlock(blockType = BlockType.PUBLISHER)) {
            BlockController.clearSessionBlock(context = Core.application, blockType = BlockType.PUBLISHER)
        }
        // reset
        update(
            appUserId = appUserId,
            sdkUserId = sdkUserId,
            accessToken = accessToken,
            ageVerifiedType = ageVerifiedType
        )
    }


    private object Preference {
        private const val preferenceName = "cash-block:account:setting"
        private val pref: SharedPreferences by lazy {
            Core.application.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        }

        private val APP_USER_ID = "app-user-id"
        var appUserId: String
            get() {
                return pref.getString(APP_USER_ID, "") ?: ""
            }
            set(value) {
                pref.edit { putString(APP_USER_ID, value) }
            }

        private val SDK_USER_ID = "sdk-user-id"
        var sdkUserId: String
            get() {
                return pref.getString(SDK_USER_ID, "") ?: ""
            }
            set(value) {
                pref.edit { putString(SDK_USER_ID, value) }
            }

        private val ACCESS_TOKEN = "access-token"
        var accessToken: String
            get() {
                return pref.getString(ACCESS_TOKEN, "") ?: ""
            }
            set(value) {
                pref.edit { putString(ACCESS_TOKEN, value) }
            }

        private val AGE_VERIFIED = "age-verified"
        var ageVerifiedType: Int
            get() {
                return pref.getInt(AGE_VERIFIED, -1)
            }
            set(value) {
                pref.edit { putInt(AGE_VERIFIED, value) }
            }

        fun clear() {
            arrayOf(
                APP_USER_ID,
                SDK_USER_ID,
                ACCESS_TOKEN,
                AGE_VERIFIED
            ).forEach { element ->
                pref.let {
                    it.edit { remove(element) }
                }
            }
        }
    }
}