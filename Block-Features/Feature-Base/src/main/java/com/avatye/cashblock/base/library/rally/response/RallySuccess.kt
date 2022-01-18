package com.avatye.cashblock.base.library.rally.response

internal data class RallySuccess(
    val statusCode: Int,
    val header: HashMap<String, String>? = null,
    val body: String
)