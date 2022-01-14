package com.avatye.cashblock.base.library.rally

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.avatye.cashblock.base.MODULE_NAME
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.base.library.rally.queue.RallyQueue
import com.avatye.cashblock.base.library.rally.queue.RallyQueueResult
import com.avatye.cashblock.base.library.rally.queue.RallyRequestQueue
import com.avatye.cashblock.base.library.rally.request.RallyRequest
import com.avatye.cashblock.base.library.rally.response.RallyFailure
import com.avatye.cashblock.base.library.rally.response.RallyResponse
import com.avatye.cashblock.base.library.rally.response.RallySuccess
import org.json.JSONObject
import java.net.URLEncoder

internal class RallyTask(private val context: Context, private val request: RallyRequest, private val response: RallyResponse) {

    fun createRallyRequestQueue(): RallyRequestQueue {
        return RallyRequestQueue(
            queue = RallyQueue(
                reqTag = request.reqTag,
                reqMethod = request.reqMethod,
                reqUrl = makeRequestUrl(),
                reqHeader = request.reqHeader,
                reqBody = makeRequestBody(),
                resListener = successListener,
                errorListener = errorListener
            )
        )
    }

    private val successListener = Response.Listener<RallyQueueResult> {
        rallyTaskTracer(responseSuccess = it)
        response.onSuccess(RallySuccess(statusCode = it.statusCode, header = it.header, body = it.body))
    }

    private val errorListener = Response.ErrorListener { res ->
        val failure = if (res.networkResponse != null) {
            RallyFailure(
                statusCode = res.networkResponse.statusCode,
                reason = res.message ?: "",
                header = res.networkResponse.headers as? HashMap<String, String>,
                body = makeErrorBody(res.networkResponse.data)
            )
        } else {
            if (res.message.equals(other = "java.io.IOException: No authentication challenges found", ignoreCase = true)) {
                RallyFailure(statusCode = 401, reason = "java.io.IOException: No authentication challenges found")
            } else {
                RallyFailure(statusCode = 999, reason = res.message ?: "unknown error")
            }
        }
        rallyTaskTracer(responseFailure = failure)
        response.onFailure(failure)
    }

    private fun makeErrorBody(response: ByteArray?): JSONObject? {
        var errorResponse: JSONObject? = null
        response?.let {
            val plainText = String(it, Charsets.UTF_8)
            if (plainText.isNotEmpty()) {
                errorResponse = JSONObject(plainText)
            }
        }
        return errorResponse
    }

    private fun makeRequestUrl(): String {
        return if (request.reqMethod == Request.Method.POST || request.reqMethod == Request.Method.PUT) {
            request.reqUrl
        } else {
            val query = makeQueryString()
            if (query.isNotEmpty()) {
                "${request.reqUrl}?$query"
            } else {
                request.reqUrl
            }
        }
    }

    private fun makeQueryString(): String {
        var query = ""
        request.reqBody?.let {
            query = try {
                val sb = StringBuilder()
                for ((key, value) in it) {
                    val keyName = makeUrlEncode(key)
                    if (keyName.isNotEmpty()) {
                        if (sb.isNotEmpty()) {
                            sb.append("&")
                        }
                        sb.append(makeUrlEncode(key))
                        sb.append('=')
                        sb.append(makeUrlEncode(value.toString()))
                    }
                }
                sb.toString()
            } catch (e: Exception) {
                ""
            }
        }
        return query
    }

    private fun makeRequestBody(): ByteArray? {
        return if (request.reqMethod == Request.Method.POST || request.reqMethod == Request.Method.PUT) {
            val jsonBodyString = makeJSONBodyString()
            try {
                jsonBodyString.toByteArray(charset = Charsets.UTF_8)
            } catch (e: Exception) {
                LogHandler.e(throwable = e, moduleName = MODULE_NAME) {
                    "RallyTask -> makeBodyBytes() -> Exception { error: ${e.message}, url: ${request.reqUrl}, object: [$jsonBodyString] }"
                }
                null
            }
        } else {
            null
        }
    }

    private fun makeJSONBodyString(): String {
        request.reqBody?.let {
            val jsonObject = JSONObject()
            for ((key, value) in it) {
                jsonObject.put(key, value)
            }
            return jsonObject.toString()
        } ?: return "{}"
    }

    // region { Network Trace }
    private fun rallyTaskTracer(responseSuccess: RallyQueueResult? = null, responseFailure: RallyFailure? = null) {
        val logBuilder = StringBuilder()
        logBuilder.appendLine("RelayTask =>")
        logBuilder.appendLine("{")
        logBuilder.appendLine("\trequest {")
        logBuilder.appendLine("\t\ttag: ${request.reqTag},")
        logBuilder.appendLine("\t\tmethod: ${fromMethodName(request.reqMethod)},")
        logBuilder.appendLine("\t\turl: ${request.reqUrl},")
        logBuilder.appendLine("\t\theader: ${request.reqHeader},")
        logBuilder.appendLine("\t\tbody: ${request.reqBody}")
        logBuilder.appendLine("\t},")
        responseSuccess?.let {
            logBuilder.appendLine("\tresponse -> Success {")
            logBuilder.appendLine("\t\tstatus: ${it.statusCode},")
            logBuilder.appendLine("\t\theader: ${it.header},")
            logBuilder.appendLine("\t\tbody: ${it.body}")
        }
        responseFailure?.let {
            logBuilder.appendLine("\tresponse -> Failure {")
            logBuilder.appendLine("\t\tstatus: ${it.statusCode},")
            logBuilder.appendLine("\t\theader: ${it.header},")
            logBuilder.appendLine("\t\tbody: ${it.body},")
            logBuilder.appendLine("\t\treason: ${it.reason}")
        }
        logBuilder.appendLine("\t}")
        logBuilder.appendLine("}")
        LogHandler.i(moduleName = MODULE_NAME) {
            logBuilder.toString()
        }
    }
    // endregion


    private fun fromMethodName(value: Int): String {
        return when (value) {
            -1 -> "DEPRECATED_GET_OR_POST"
            0 -> "GET"
            1 -> "POST"
            2 -> "PUT"
            3 -> "DELETE"
            4 -> "HEAD"
            5 -> "OPTIONS"
            6 -> "TRACE"
            7 -> "PATCH"
            else -> "UNKNOWN:$value"
        }
    }

    private fun makeUrlEncode(value: String?): String {
        var encoded: String = ""
        value?.let {
            kotlin.runCatching {
                URLEncoder.encode(value, "UTF-8")
            }.onSuccess {
                encoded = it
            }
        }
        return encoded
    }
}