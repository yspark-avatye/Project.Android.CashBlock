package com.avatye.cashblock.base.library.rally.queue

internal data class RallyQueueResult(
    val statusCode: Int,
    val header: HashMap<String, String>? = null,
    val body: String
)