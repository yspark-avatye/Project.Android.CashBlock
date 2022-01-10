package com.avatye.cashblock.base.component.support

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.provider.Settings

object DeviceUtil {

    // region # battery optimize
    object Battery {
        fun isOptimization(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val manager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                manager.isIgnoringBatteryOptimizations(context.packageName)
            } else {
                true
            }
        }

        fun requestOptimization(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
            }
        }
    }
    // endregion
}