package com.avatye.cashblock.base.internal.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.avatye.cashblock.base.block.BlockController
import com.avatye.cashblock.base.FeatureCore
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.user.AgeVerifiedType

internal object AccountPreferenceData {
    private var valueOfAppUserId = Preference.appUserId
    private var valueOfSDKUserId = Preference.sdkUserId
    private var valueOfAccessToken = Preference.accessToken
    private var valueOfAgeVerifiedType = Preference.ageVerifiedType

    val isValid: Boolean
        get() {
            return valueOfAppUserId.isNotEmpty()
                    && valueOfSDKUserId.isNotEmpty()
                    && valueOfAccessToken.isNotEmpty()
        }

    val appUserId: String
        get() {
            return valueOfAppUserId
        }

    val sdkUserId: String
        get() {
            return valueOfSDKUserId
        }

    val accessToken: String
        get() {
            return valueOfAccessToken
        }

    val ageVerified: AgeVerifiedType
        get() {
            return AgeVerifiedType.from(valueOfAgeVerifiedType)
        }

    fun needUpdateAppUserId(targetAppUserId: String): Boolean {
        return !targetAppUserId.equals(other = appUserId, ignoreCase = false)
    }

    fun update(appUserId: String? = null, sdkUserId: String? = null, accessToken: String? = null, ageVerifiedType: AgeVerifiedType? = null) {
        appUserId?.let {
            valueOfAppUserId = it
            Preference.appUserId = it
        }
        sdkUserId?.let {
            valueOfSDKUserId = it
            Preference.sdkUserId = it
        }
        accessToken?.let {
            valueOfAccessToken = it
            Preference.accessToken = it
        }
        ageVerifiedType?.let {
            valueOfAgeVerifiedType = it.value
            Preference.ageVerifiedType = it.value
        }
    }

    fun clearSession(appUserId: String? = null, sdkUserId: String? = null, accessToken: String? = null, ageVerifiedType: AgeVerifiedType? = null) {
        // clear core session
        Preference.clear()
        // clear connector session
        if (BlockController.hasBlock(blockType = BlockType.PUBLISHER)) {
            BlockController.clearSessionBlock(context = FeatureCore.application, blockType = BlockType.PUBLISHER)
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
            FeatureCore.application.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
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