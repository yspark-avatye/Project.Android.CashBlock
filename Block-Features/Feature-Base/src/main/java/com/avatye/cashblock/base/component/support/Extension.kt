package com.avatye.cashblock.base.component.support

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.*
import android.provider.Settings
import android.text.Html
import android.text.Spanned
import android.util.Base64
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import org.joda.time.DateTime
import org.joda.time.Period
import java.net.URLEncoder
import java.text.NumberFormat
import java.util.*

// region { String-Encode }
val String.toUrlEncode: String
    get() {
        return try {
            URLEncoder.encode(this, "UTF-8")
        } catch (e: Exception) {
            ""
        }
    }

val String.toBase64: String
    get() {
        return Base64.encodeToString(this.toByteArray(charset("UTF-8")), Base64.NO_WRAP)
    }

fun String?.takeIfNullOrEmpty(block: () -> String): String {
    return if (this.isNullOrEmpty()) block() else this
}
// endregion


// region { View }
fun View.setOnClickWithDebounce(debounceTime: Long = 500L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            val elapsedRealtime = SystemClock.elapsedRealtime()
            if (elapsedRealtime - lastClickTime < debounceTime) {
                return
            } else {
                action()
            }
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}
// endregion


// region { TextView }
fun TextView.compoundDrawablesWithIntrinsicBounds(
    @DrawableRes start: Int = 0,
    @DrawableRes top: Int = 0,
    @DrawableRes end: Int = 0,
    @DrawableRes bottom: Int = 0
) {
    this.setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom)
}
// endregion


// region { Activity }
fun <T : Parcelable> Activity.extraParcel(key: String): T? {
    return intent?.extras?.getParcelable(key)
}

fun Activity.extraString(key: String): String? {
    return intent?.extras?.getString(key)
}

fun Activity.extraInt(key: String): Int? {
    return intent?.extras?.getInt(key)
}

fun Activity.extraLong(key: String): Long? {
    return intent?.extras?.getLong(key)
}

fun Activity.extraBoolean(key: String): Boolean? {
    return intent?.extras?.getBoolean(key)
}

fun Activity.extraFloat(key: String): Float? {
    return intent?.extras?.getFloat(key)
}

val Activity?.isAlive: Boolean
    get() = !(this?.isFinishing ?: true)

fun Activity.launch(intent: Intent, transition: Pair<Int, Int>? = null, close: Boolean = false, options: Bundle? = null) {
    startActivity(intent, options)
    transition?.let { overridePendingTransition(it.first, it.second) }
    if (close) {
        finish()
    }
}

fun Activity.launchFortResult(intent: Intent, requestCode: Int, transition: Pair<Int, Int>? = null, options: Bundle? = null) {
    startActivityForResult(intent, requestCode, options)
    transition?.let { overridePendingTransition(it.first, it.second) }
}
// endregion


// region { context }
val Context.hostPackageName: String
    get() = this.applicationInfo.packageName

val Context.hostAppName: String
    get() = this.packageManager.getApplicationLabel(this.applicationInfo).toString()

val Context.hostAppVersionName: String
    get() = this.packageManager.getPackageInfo(this.packageName, 0).versionName

val Context.hostAppVersionCode: String
    get() {
        return try {
            if (packageManager != null && packageName != null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    (packageManager!!.getPackageInfo(packageName!!, 0).versionCode).toString()
                } else {
                    packageManager!!.getPackageInfo(packageName!!, 0).longVersionCode.toString()
                }
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }

val Context.isScreenOn: Boolean
    get() {
        val powerManager = this.getSystemService(Context.POWER_SERVICE) as PowerManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            powerManager.isInteractive
        } else {
            powerManager.isScreenOn
        }
    }

val Context.connectivityManager: ConnectivityManager
    get() = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

@get:SuppressLint("MissingPermission")
val Context.connectivityTypeName: String
    get() {
        val manager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = manager.activeNetwork
            val capabilities = manager.getNetworkCapabilities(network)
            return if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "wifi"
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "mobile"
                    else -> ""
                }
            } else {
                ""
            }
        } else {
            val wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            if (wifi?.isConnected == true) "wifi" else "mobile"
        }
    }

val Context.isAlwaysFinishActivitiesEnabled: Boolean
    get() {
        val state = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Settings.System.getInt(
                this.contentResolver,
                Settings.System.ALWAYS_FINISH_ACTIVITIES,
                0
            )
        } else {
            Settings.Global.getInt(
                this.contentResolver,
                Settings.Global.ALWAYS_FINISH_ACTIVITIES,
                0
            )
        }
        return state != 0
    }
// endregion


// region { String }
val String.toHtml: Spanned
    get() {
        return if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(this)
        } else {
            Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
        }
    }
// endregion


// region { boolean/int }
fun Int.toBoolean(): Boolean = (this == 1)

fun Boolean.toInt(): Int = if (this) 1 else 0

fun Int.toLocale(inLocale: Locale = Locale.KOREA): String {
    return try {
        NumberFormat.getInstance(inLocale).format(this)
    } catch (e: Exception) {
        ""
    }
}

fun Int.toLocaleOver(over: Int): String {
    return when {
        (over > 0 && this >= over) -> over.toLocale().plus("+")
        else -> this.toLocale()
    }
}
// endregion


// region { DP / Pixel }
val Int.toPX: Float get() = (this * Resources.getSystem().displayMetrics.density + 0.5f)
val Float.toPX: Float get() = (this * Resources.getSystem().displayMetrics.density + 0.5f)
val Int.toDP: Float get() = (this / Resources.getSystem().displayMetrics.density)
val Float.toDP: Float get() = (this / Resources.getSystem().displayMetrics.density)
// endregion


// region { meta-data }
fun Context.metaDataBundle(): Bundle? {
    return try {
        packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA).metaData
    } catch (e: Exception) {
        null
    }
}


fun Context.metaDataValue(keyName: String): String? {
    return try {
        val bundle = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA).metaData
        val bundleValue = bundle[keyName]?.toString() ?: ""
        if (bundleValue.isEmpty()) null else bundleValue
    } catch (e: Exception) {
        null
    }
}


fun Context.metaDataValue(keyName: String, defaultValue: String): String {
    return this.metaDataValue(keyName = keyName).takeIfNullOrEmpty { defaultValue }
}


fun Context.metaDataVerify(keyName: String, callback: (value: String) -> Unit = {}) {
    metaDataValue(keyName)?.let {
        callback(it)
    } ?: run {
        throw Exception("AndroidManifest: $keyName is null or empty")
    }
}
// endregion


// region { DateTime }
fun DateTime.toFeedTime(): String {
    // 2day return
    if (this.millis < DateTime().minusDays(2).millis) {
        return this.toString("yyyy.MM.dd HH:mm")
    }
    // calculate period
    var resultTimeString = ""
    try {
        val currentDateTime = DateTime()
        val period: Period = if (currentDateTime.isAfter(this)) {
            Period(this, currentDateTime)
        } else {
            Period(currentDateTime, this)
        }
        when (period.toStandardDays().days) {
            0 -> {
                when {
                    // check seconds
                    period.toStandardSeconds().seconds < 60 -> {
                        resultTimeString = "방금"
                    }
                    // check minutes
                    period.toStandardMinutes().minutes < 60 -> {
                        resultTimeString = "${period.toStandardMinutes().minutes}분 전"
                    }
                    // check hours
                    period.toStandardHours().hours < 24 -> {
                        resultTimeString = "${period.toStandardHours().hours}시간 전"
                    }
                }
            }
            1 -> resultTimeString = "어제 ${this.toString("HH:mm")}"
            else -> resultTimeString = this.toString("yyyy.MM.dd HH:mm")
        }
    } catch (exception: Exception) {
        resultTimeString = this.toString("yyyy.MM.dd HH:mm")
    }
    return resultTimeString
}

fun DateTime.toFeedTimeShort(): String {
    // 2day return
    if (this.millis < DateTime().minusDays(2).millis) {
        return this.toString("yyyy.MM.dd")
    }
    // calculate period
    var resultTimeString = ""
    try {
        val currentDateTime = DateTime()
        val period: Period = if (currentDateTime.isAfter(this)) {
            Period(this, currentDateTime)
        } else {
            Period(currentDateTime, this)
        }
        when (period.toStandardDays().days) {
            0 -> {
                when {
                    // check seconds
                    period.toStandardSeconds().seconds < 60 -> {
                        resultTimeString = "방금"
                    }
                    // check minutes
                    period.toStandardMinutes().minutes < 60 -> {
                        resultTimeString = "${period.toStandardMinutes().minutes}분 전"
                    }
                    // check hours
                    period.toStandardHours().hours < 24 -> {
                        resultTimeString = "${period.toStandardHours().hours}시간 전"
                    }
                }
            }
            1 -> resultTimeString = "어제 ${this.toString("HH:mm")}"
            else -> resultTimeString = this.toString("yyyy.MM.dd")
        }
    } catch (exception: Exception) {
        resultTimeString = this.toString("yyyy.MM.dd")
    }
    return resultTimeString
}
// endregion