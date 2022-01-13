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
    }
}