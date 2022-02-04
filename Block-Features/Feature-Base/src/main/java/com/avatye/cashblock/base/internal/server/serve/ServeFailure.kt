package com.avatye.cashblock.base.internal.server.serve

import org.json.JSONObject

internal data class ServeFailure(
    val statusCode: Int = 0,
    private val serverCode: String = "",
    private val message: String = "",
    private val body: JSONObject? = null
) {
    val errorCode: String get() = serverCode

    val errorMessage: String
        get() {
            return when {
                message.isEmpty() -> "네트워크가 연결되어 있지 않거나 일시적인 오류가 발생하였습니다.\\n잠시 후 다시 시도해 주세요."
                else -> message
            }
        }

    val isUnauthenticated: Boolean get() = statusCode == 401

    val isForbidden: Boolean get() = statusCode == 403

    val isInspection: Boolean get() = (statusCode == 503 || statusCode == 504)

    override fun toString(): String {
        return "{ statusCode: ${statusCode}, serverCode: $serverCode, message: $message, body: $body }"
    }
}