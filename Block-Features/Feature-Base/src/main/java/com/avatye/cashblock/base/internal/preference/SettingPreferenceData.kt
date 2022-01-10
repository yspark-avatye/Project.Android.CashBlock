package com.avatye.cashblock.base.internal.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.avatye.cashblock.base.FeatureCore

internal object SettingPreferenceData {

    private var valueOfNeedNotifyUserAAID = Preference.needNotifyUseAAID
    val needNotifyUseAAID: Boolean
        get() {
            return valueOfNeedNotifyUserAAID
        }


    private var valueOfPopupNoticeCloseDate = Preference.popupNoticeCloseDate
    val popupNoticeCloseDate: String
        get() {
            return valueOfPopupNoticeCloseDate
        }

    private var valueOfDeviceAAID = Preference.deviceAAID
    val deviceAAID: String
        get() {
            return valueOfDeviceAAID
        }

    fun update(
        needNotifyUseAAID: Boolean? = null,
        popupNoticeCloseDate: String? = null,
        deviceAAID: String? = null
    ) {
        needNotifyUseAAID?.let {
            valueOfNeedNotifyUserAAID = it
            Preference.needNotifyUseAAID = it
        }
        popupNoticeCloseDate?.let {
            valueOfPopupNoticeCloseDate = it
            Preference.popupNoticeCloseDate = it
        }
        deviceAAID?.let {
            valueOfDeviceAAID = it
            Preference.deviceAAID = it
        }
    }

    // region { preference }
    private object Preference {
        private const val preferenceName = "cash-block:core:setting"
        private val pref: SharedPreferences by lazy {
            FeatureCore.application.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        }

        private const val NEED_NOTIFY_USE_AAID = "need-notify-use-aaid"
        var needNotifyUseAAID: Boolean
            get() {
                return pref.getBoolean(NEED_NOTIFY_USE_AAID, true)
            }
            set(value) {
                pref.edit { putBoolean(NEED_NOTIFY_USE_AAID, value) }
            }

        private const val POPUP_NOTICE_CLOSE_DATE = "popup-notice:close-date"
        var popupNoticeCloseDate: String
            get() {
                return pref.getString(POPUP_NOTICE_CLOSE_DATE, "") ?: ""
            }
            set(value) {
                pref.edit { putString(POPUP_NOTICE_CLOSE_DATE, value) }
            }

        // region # mission
        private const val MISSION_ATTENDANCE_CHECK_DATE = "mission:attendance:check-date"
        var attendanceCheckDate: String
            get() {
                return pref.getString(MISSION_ATTENDANCE_CHECK_DATE, "") ?: ""
            }
            set(value) {
                pref.edit { putString(MISSION_ATTENDANCE_CHECK_DATE, value) }
            }

        private const val MISSION_ATTENDANCE_SHOW_DATE = "mission:attendance:show-date"
        var attendanceShowDate: String
            get() {
                return pref.getString(MISSION_ATTENDANCE_SHOW_DATE, "") ?: ""
            }
            set(value) {
                pref.edit { putString(MISSION_ATTENDANCE_SHOW_DATE, value) }
            }

        private const val MISSION_ATTENDANCE_COMPLETE = "mission:attendance:complete"
        var attendanceComplete: Boolean
            get() {
                return pref.getBoolean(MISSION_ATTENDANCE_COMPLETE, false)
            }
            set(value) {
                pref.edit { putBoolean(MISSION_ATTENDANCE_COMPLETE, value) }
            }
        // endregion

        // region # AAID
        private const val PLATFORM_DEVICE_AAID = "platform:device:aaid"
        var deviceAAID: String
            get() {
                return pref.getString(PLATFORM_DEVICE_AAID, "") ?: ""
            }
            set(value) {
                pref.edit { putString(PLATFORM_DEVICE_AAID, value) }
            }
        // endregion
    }
    // endregion
}