package com.avatye.cashblock.base.library.miscellaneous

import org.joda.time.DateTime
import org.json.JSONArray
import org.json.JSONObject

// region { JSON }
@Throws(Exception::class)
fun JSONObject.toStringValue(name: String, default: String = ""): String {
    return when {
        this.has(name) && !this.isNull(name) -> this.getString(name)
        else -> default
    }
}

@Throws(Exception::class)
fun JSONObject.toStringValue(firstName: String, secondName: String, default: String = ""): String {
    return if (this.has(firstName) && !this.isNull(firstName)) {
        this.getString(firstName)
    } else if (this.has(secondName) && !this.isNull(secondName)) {
        this.getString(secondName)
    } else {
        default
    }
}

@Throws(Exception::class)
fun JSONObject.toIntValue(name: String, default: Int = 0): Int {
    return when {
        this.has(name) && !this.isNull(name) -> this.getInt(name)
        else -> default
    }
}

@Throws(Exception::class)
fun JSONObject.toFloatValue(name: String, default: Float = 0f): Float {
    return when {
        this.has(name) && !this.isNull(name) -> this.getDouble(name).toFloat()
        else -> default
    }
}

@Throws(Exception::class)
fun JSONObject.toLongValue(name: String, default: Long = 0L): Long {
    return when {
        this.has(name) && !this.isNull(name) -> this.getLong(name)
        else -> default
    }
}

@Throws(Exception::class)
fun JSONObject.toDoubleValue(name: String, default: Double = 0.0): Double {
    return when {
        this.has(name) && !this.isNull(name) -> this.getDouble(name)
        else -> default
    }
}

@Throws(Exception::class)
fun JSONObject.toBooleanValue(name: String, default: Boolean = false): Boolean {
    return when {
        this.has(name) && !this.isNull(name) -> {
            val innerValue = this.getString(name)
            when (innerValue.lowercase()) {
                "1", "true" -> true
                "0", "false" -> false
                else -> default
            }
        }
        else -> default
    }
}

@Throws(Exception::class)
fun JSONObject.toDateTimeValue(name: String, default: DateTime? = null): DateTime? {
    val dateValue = this.toStringValue(name, "")
    return if (dateValue.isNotEmpty()) {
        DateTime(dateValue)
    } else {
        default
    }
}

@Throws(Exception::class)
fun JSONObject.toJSONObjectValue(name: String): JSONObject? {
    return when {
        this.has(name) && !this.isNull(name) -> this.getJSONObject(name)
        else -> null
    }
}

@Throws(Exception::class)
fun JSONObject.toJSONArrayValue(name: String): JSONArray? {
    return when {
        this.has(name) && !this.isNull(name) -> this.getJSONArray(name)
        else -> null
    }
}

fun JSONArray.isEmpty(): Boolean {
    return this.length() == 0
}

fun JSONArray.until(loop: (json: JSONObject) -> Unit) {
    val length = this.length()
    for (i in 0 until length) {
        loop(this.getJSONObject(i))
    }
}

fun JSONArray.untilAny(loop: (i: Any) -> Unit) {
    val length = this.length()
    for (i in 0 until length) {
        loop(this.get(i))
    }
}

fun JSONArray.until(feasibility: (feasible: Boolean) -> Unit, loop: (json: JSONObject) -> Unit) {
    // size
    val length = this.length()
    // call -> feasibility
    feasibility(length > 0)
    // call -> loop
    for (i in 0 until length) {
        loop(this.getJSONObject(i))
    }
}

fun JSONArray.untilWithIndex(loop: (index: Int, json: JSONObject) -> Unit) {
    val length = this.length()
    for (i in 0 until length) {
        loop(i, this.getJSONObject(i))
    }
}
// endregion