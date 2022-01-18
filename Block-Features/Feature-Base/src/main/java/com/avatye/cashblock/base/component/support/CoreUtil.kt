package com.avatye.cashblock.base.component.support

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.avatye.cashblock.base.Core
import com.avatye.cashblock.base.MODULE_NAME
import com.avatye.cashblock.R
import com.avatye.cashblock.base.library.LogHandler
import java.util.*

object CoreUtil {

    fun appMetaDataList(context: Context): HashMap<String, String> {
        val result = HashMap<String, String>()
        try {
            val bundle = context.metaDataBundle()
            bundle?.keySet()?.forEach {
                result[it] = "${bundle[it]}"
            }
        } catch (e: Exception) {
            LogHandler.e(throwable = e, moduleName = MODULE_NAME)
        }
        return result
    }

    // region { battery optimization }
    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val manager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            manager.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true
        }
    }

    fun requestIgnoringBatteryOptimizations(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
        }
    }
    // endregion

    // region # toast
    fun showToast(message: String, isShowLong: Boolean = false) {
        Toast(Core.application).let {
            it.duration = if (isShowLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            it.view = LayoutInflater.from(Core.application).inflate(R.layout.acb_common_widget_toast, null).apply {
                findViewById<TextView>(R.id.toast_text).text = message
            }
            it.show()
        }
    }

    fun showToast(@StringRes message: Int, isShowLong: Boolean = false) {
        Toast(Core.application).let {
            it.duration = if (isShowLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            it.view = LayoutInflater.from(Core.application).inflate(R.layout.acb_common_widget_toast, null).apply {
                findViewById<TextView>(R.id.toast_text).setText(message)
            }
            it.show()
        }
    }
    // endregion
}