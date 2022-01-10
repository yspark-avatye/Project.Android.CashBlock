package com.avatye.cashblock.publisher.component.data

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.Keep
import androidx.core.content.edit
import com.avatye.cashblock.base.component.contract.CoreContract
import com.avatye.cashblock.publisher.PublisherConfig.logger

@Keep
object PreferenceData {

    private var _publisherId = Preference.publisherId
    val publisherId: String get() = _publisherId

    private var _publisherAppKey = Preference.publisherAppKey
    val publisherAppKey: String get() = _publisherAppKey

    private var _publisherRetryEndTime = Preference.publisherRetryEndTime
    val publisherRetryEndTime: Long get() = _publisherRetryEndTime

    private var _publisherBlockAppId = Preference.publisherBlockAppId
    val publisherBlockAppId: String get() = _publisherBlockAppId

    private var _publisherBlockAppSecret = Preference.publisherBlockAppSecret
    val publisherBlockAppSecret: String get() = _publisherBlockAppSecret

    fun update(
        publisherId: String? = null,
        publisherAppKey: String? = null,
        publisherRetryEndTime: Long? = null,
        publisherBlockAppId: String? = null,
        publisherBlockAppSecret: String? = null
    ) {
        publisherId?.let {
            _publisherId = it
            Preference.publisherId = it
        }
        publisherAppKey?.let {
            _publisherAppKey = it
            Preference.publisherAppKey = it
        }
        publisherRetryEndTime?.let {
            _publisherRetryEndTime = it
            Preference.publisherRetryEndTime = it
        }
        publisherBlockAppId?.let {
            _publisherBlockAppId = it
            Preference.publisherBlockAppId = it
        }
        publisherBlockAppSecret?.let {
            _publisherBlockAppSecret = it
            Preference.publisherBlockAppSecret = it
        }
    }

    fun clear() {
        Preference.clear()
        update(
            publisherId = Preference.publisherId,
            publisherAppKey = Preference.publisherAppKey,
            publisherRetryEndTime = Preference.publisherRetryEndTime,
            publisherBlockAppId = Preference.publisherBlockAppId,
            publisherBlockAppSecret = Preference.publisherBlockAppSecret
        )
    }

    // region # Preference
    private object Preference {
        private const val preferenceName = "block:publisher:local-setting"
        private val pref: SharedPreferences by lazy {
            CoreContract.coreContext.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        }

        private const val PUBLISHER_ID = "publisher:app:id"
        var publisherId: String
            get() {
                return pref.getString(PUBLISHER_ID, "") ?: ""
            }
            set(value) {
                pref.edit { putString(PUBLISHER_ID, value) }
            }


        private const val PUBLISHER_APP_KEY = "publisher:app:key"
        var publisherAppKey: String
            get() {
                return pref.getString(PUBLISHER_APP_KEY, "") ?: ""
            }
            set(value) {
                pref.edit { putString(PUBLISHER_APP_KEY, value) }
            }


        private const val PUBLISHER_SETTING_RETRY_END_TIME = "publisher:setting:retry-end-time"
        var publisherRetryEndTime: Long
            get() {
                return pref.getLong(PUBLISHER_SETTING_RETRY_END_TIME, 0L)
            }
            set(value) {
                pref.edit { putLong(PUBLISHER_SETTING_RETRY_END_TIME, value) }
            }


        private const val PUBLISHER_BLOCK_APP_ID = "publisher:block:app-id"
        var publisherBlockAppId: String
            get() {
                return pref.getString(PUBLISHER_BLOCK_APP_ID, "") ?: ""
            }
            set(value) {
                pref.edit { putString(PUBLISHER_BLOCK_APP_ID, value) }
            }


        private const val PUBLISHER_BLOCK_APP_SECRET = "publisher:block:app-secret"
        var publisherBlockAppSecret: String
            get() {
                return pref.getString(PUBLISHER_BLOCK_APP_SECRET, "") ?: ""
            }
            set(value) {
                pref.edit { putString(PUBLISHER_BLOCK_APP_SECRET, value) }
            }

        fun clear() {
            arrayOf(PUBLISHER_ID, PUBLISHER_APP_KEY, PUBLISHER_SETTING_RETRY_END_TIME, PUBLISHER_BLOCK_APP_ID, PUBLISHER_BLOCK_APP_SECRET).forEach { keyName ->
                try {
                    pref.edit { remove(keyName) }
                } catch (e: Exception) {
                    logger.e(throwable = e) {
                        "PreferenceData -> clear() { PUBLISHER_ID, PUBLISHER_APP_KEY, PUBLISHER_SETTING_RETRY_END_TIME, PUBLISHER_BLOCK_APP_ID, PUBLISHER_BLOCK_APP_SECRET }"
                    }
                }
            }
        }
        // endregion
    }
}