package com.avatye.cashblock.base.internal.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.avatye.cashblock.base.Core
import com.avatye.cashblock.base.block.BlockController
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.user.AgeVerifiedType
import com.avatye.cashblock.base.component.domain.entity.user.GenderType
import com.avatye.cashblock.base.component.domain.entity.user.Profile

internal object AccountPreferenceData {
    // region # profile
    private var _appUserId = Preference.appUserId
    private var _appUserBirthYear = Preference.appUserBirthYear
    private var _appUserGender = Preference.appUserGender
    // endregion

    private var _sdkUserId = Preference.sdkUserId
    private var _accessToken = Preference.accessToken
    private var _ageVerifiedType = Preference.ageVerifiedType

    val isValid: Boolean
        get() {
            return profile.isValid()
                    && _sdkUserId.isNotEmpty()
                    && _accessToken.isNotEmpty()
        }

    val profile: Profile
        get() {
            return Profile(
                userId = _appUserId,
                gender = _appUserGender,
                birthYear = _appUserBirthYear
            )
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

    fun needUpdateAppUserProfile(profile: Profile): Boolean {
        return this.profile != profile
    }

    fun update(profile: Profile? = null, sdkUserId: String? = null, accessToken: String? = null, ageVerifiedType: AgeVerifiedType? = null) {
        profile?.let {
            // cache
            _appUserId = it.userId
            _appUserGender = it.gender
            _appUserBirthYear = it.birthYear
            // date
            Preference.appUserId = it.userId
            Preference.appUserGender = it.gender
            Preference.appUserBirthYear = it.birthYear
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

    fun clearProfile() {
        // cache
        _appUserId = ""
        _appUserGender = GenderType.MALE
        _appUserBirthYear = 0
        // data
        Preference.appUserId = _appUserId
        Preference.appUserGender = _appUserGender
        Preference.appUserBirthYear = _appUserBirthYear
    }

    fun clearSession(profile: Profile? = null, sdkUserId: String? = null, accessToken: String? = null, ageVerifiedType: AgeVerifiedType? = null) {
        // clear core session
        Preference.clear()
        // clear connector session
        if (BlockController.hasBlock(blockType = BlockType.PUBLISHER)) {
            BlockController.clearSessionBlock(context = Core.application, blockType = BlockType.PUBLISHER)
        }
        // reset
        update(
            profile = profile,
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

        private val APP_USER_GENDER = "app-user-gender"
        var appUserGender: GenderType
            get() {
                return GenderType.from(pref.getString(APP_USER_GENDER, "M") ?: "M") ?: GenderType.MALE
            }
            set(value) {
                pref.edit { putString(APP_USER_GENDER, value.value) }
            }

        private val APP_USER_BIRTH_YEAR = "app-user-birth-year"
        var appUserBirthYear: Int
            get() {
                return pref.getInt(APP_USER_BIRTH_YEAR, 0)
            }
            set(value) {
                pref.edit { putInt(APP_USER_BIRTH_YEAR, value) }
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
                APP_USER_GENDER,
                APP_USER_BIRTH_YEAR,
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