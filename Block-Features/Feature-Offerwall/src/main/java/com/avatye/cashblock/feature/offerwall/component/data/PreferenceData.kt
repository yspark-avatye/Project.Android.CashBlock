package com.avatye.cashblock.feature.offerwall.component.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.feature.offerwall.MODULE_NAME
import com.avatye.cashblock.feature.offerwall.OfferwallConfig
import org.json.JSONObject

internal object PreferenceData {
    private const val tagName = "PreferenceData@Offerwall"

    // region # popup-notice
    object PopupNotice {
        private var PopupCloseDate: LinkedHashMap<String, Int> = makePopupDataCollection(Preference.PopupNotice.popupCloseDate)
        val popupCloseDate: LinkedHashMap<String, Int> get() = PopupCloseDate

        fun clear() = Preference.PopupNotice.clear()

        fun update(popupCloseDate: Map<String, Int>? = null) {
            popupCloseDate?.let {
                PopupCloseDate = it as LinkedHashMap<String, Int>
                Preference.PopupNotice.popupCloseDate = JSONObject(it).toString()
            }
        }

        private fun makePopupDataCollection(collectionRawData: String): LinkedHashMap<String, Int> {
            val resultCollection = LinkedHashMap<String, Int>()
            if (collectionRawData.isNotEmpty()) {
                try {
                    val map = JSONObject(collectionRawData)
                    map.keys().asSequence().forEach {
                        resultCollection[it] = map.getInt(it)
                    }
                } catch (e: Exception) {
                    LogHandler.e(moduleName = MODULE_NAME, throwable = e) {
                        "$tagName -> makePopupCloseMap { rawString: [$collectionRawData] }"
                    }
                }
            }
            return resultCollection
        }
    }
    // endregion

    // region # Tab
    object Tab {
        private var _conditionTime = Preference.Tab.conditionTime
        var conditionTime: Long = 0
            get() = _conditionTime

        fun clear() {
            Preference.Tab.clear()
            update(
                conditionTime = Preference.Tab.conditionTime
            )
        }

        fun update(
            conditionTime: Long
        ) {
            conditionTime.let {
                _conditionTime = it
                Preference.Tab.conditionTime = it
            }
        }
    }
    // endregion

    // region # Hidden
    object Hidden {
        private var _hiddenSections = Preference.Hidden.hiddenSections
        val hiddenSections: List<String>?
            get() = _hiddenSections

        private var _hiddenItems = Preference.Hidden.hiddenItems
        val hiddenItems: List<String>?
            get() = _hiddenItems
    }
    // endregion


    // region # Preference
    private object Preference {
        private const val preferenceName = "block:offerwall:local-setting"
        private val pref: SharedPreferences by lazy {
            OfferwallConfig.application.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        }

        // region # popup-notice
        object PopupNotice {
            private val CLOSE_DATE = "popup-notice:close-date"
            var popupCloseDate: String
                get() {
                    return pref.getString(CLOSE_DATE, "") ?: ""
                }
                set(value) {
                    pref.edit { putString(CLOSE_DATE, value) }
                }

            fun clear() {
                arrayOf(
                    CLOSE_DATE
                ).forEach { element ->
                    pref.edit { remove(element) }
                }
            }
        }
        // endregion

        // region # Tab
        object Tab {
            private val CONDITION_TIME = "offerwall-tab:condition-time"

            var conditionTime: Long
                get() {
                    return pref.getLong(CONDITION_TIME, 0)
                }
                set(value) {
                    pref.edit { putLong(CONDITION_TIME, value) }
                }

            fun clear() {
                arrayOf(
                    CONDITION_TIME
                ).forEach { element ->
                    pref.edit { remove(element) }
                }
            }
        }
        // endregion

        // region # Hidden
        object Hidden {
            private val HIDDEN_SECTIONS = "offerwall-hidden:sections"
            private val HIDDEN_ITEMS = "offerwall-hidden:items"
            private val HIDDEN_SECIONS_CODE = "offerwall-hidden:sections-code"

            var hiddenSections: List<String>?
                get() {
                    return pref.getString(HIDDEN_SECTIONS, "")?.split(",")?.filter { it != "" }
                }
                set(value) {
                    pref.edit { putString(HIDDEN_SECTIONS, value?.joinToString(",")) }
                }

            var hiddenItems: List<String>?
                get() {
                    return pref.getString(HIDDEN_ITEMS, "")?.split(",")?.filter { it != "" }
                }
                set(value) {
                    pref.edit { putString(HIDDEN_ITEMS, value?.joinToString(",")) }
                }

            fun clear() {
                arrayOf(
                    HIDDEN_SECTIONS,
                    HIDDEN_ITEMS
                ).forEach { element ->
                    pref.edit { remove(element) }
                }
            }
        }
    }
}