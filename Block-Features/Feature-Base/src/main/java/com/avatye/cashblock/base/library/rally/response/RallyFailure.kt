package com.avatye.cashblock.base.library.rally.response

import org.json.JSONObject

data class RallyFailure(
    val statusCode: Int,
    val reason: String? = null,
    val header: HashMap<String, String>? = null,
    val body: JSONObject? = null
)