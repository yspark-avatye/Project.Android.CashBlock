package com.avatye.cashblock.base.internal.server.serve

import com.avatye.cashblock.base.library.miscellaneous.toDateTimeValue
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import com.avatye.cashblock.module.data.serve.ServeStatusType
import org.joda.time.DateTime
import org.json.JSONObject

data class ServeFailure(
    val statusCode: Int = 0,
    private val serverCode: String = "",
    private val message: String = "",
    private val body: JSONObject? = null
) {
    data class InspectionEntity(
        val startDateTime: DateTime? = null,
        val endDateTime: DateTime? = null,
        val message: String,
        val link: String
    )

    val serveStatusType: ServeStatusType
        get() {
            return when (statusCode) {
                401 -> ServeStatusType.UNAUTHENTICATED
                403 -> ServeStatusType.FORBIDDEN
                503, 504 -> ServeStatusType.INSPECTION
                else -> ServeStatusType.ERROR
            }
        }

    val errorCode: String
        get() {
            return serverCode
        }

    val errorMessage: String
        get() {
            return when {
                message.isEmpty() -> "네크워크가 연결되어 있지 않거나 일시적인 오류가 발생하였습니다.\\n잠시 후 다시 시도해 주세요."
                else -> message
            }
        }

    val inspectionData: InspectionEntity?
        get() {
            var entity: InspectionEntity? = null
            if (serveStatusType == ServeStatusType.INSPECTION) {
                body?.let {
                    entity = InspectionEntity(
                        startDateTime = it.toDateTimeValue("startDateTime"),
                        endDateTime = it.toDateTimeValue("endDateTime"),
                        message = it.toStringValue("message"),
                        link = it.toStringValue("link")
                    )
                }
            }
            return entity
        }

    override fun toString(): String {
        return "{ statusCode: ${statusCode}, serverCode: $serverCode, message: $message, body: $body }"
    }
}