package com.avatye.cashblock.base.library.ad.aaid

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import com.avatye.cashblock.base.MODULE_NAME
import com.avatye.cashblock.base.library.LogHandler
import com.google.android.gms.ads.identifier.AdvertisingIdClient

@SuppressLint("StaticFieldLeak")
class AdvertiseIDTask(
    private val context: Context,
    private val callback: (isLimitAdTrackingEnabled: Boolean, aaid: String) -> Unit
) : AsyncTask<Void, Void, AdvertisingIdClient.Info>() {

    companion object {
        private const val empty = "00000000-0000-0000-0000-000000000000"
        fun isValid(aaid: String): Boolean {
            return !(aaid.isEmpty() || aaid == "0" || aaid == empty)
        }
    }

    override fun doInBackground(vararg params: Void?): AdvertisingIdClient.Info {
        return AdvertisingIdClient.getAdvertisingIdInfo(context)
    }

    override fun onPostExecute(result: AdvertisingIdClient.Info) {
        val isLimitAdTrackingEnabled = result.isLimitAdTrackingEnabled
        val aaid = if (isLimitAdTrackingEnabled) empty else result.id ?: empty
        LogHandler.i(moduleName = MODULE_NAME) { "AdvertiseIDTask { aaid: $aaid, isLimitAdTrackingEnabled: $isLimitAdTrackingEnabled }" }
        callback(isLimitAdTrackingEnabled, aaid)
    }
}