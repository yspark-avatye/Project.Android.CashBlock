package com.avatye.cashblock.base.library.rally.request

import java.util.*

internal data class RallyRequest(
    val reqTag: String,
    val reqMethod: Int,
    val reqUrl: String,
    val reqHeader: HashMap<String, String>? = null,
    val reqBody: HashMap<String, Any>? = null
)