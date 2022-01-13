package com.avatye.cashblock.base.internal.server.serve

import android.content.Context
import android.os.Build
import com.android.volley.Request
import com.avatye.cashblock.base.Core
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.business.EventContractor
import com.avatye.cashblock.base.component.domain.entity.app.AppEnvironment
import com.avatye.cashblock.base.component.domain.entity.app.AppInspection
import com.avatye.cashblock.base.component.support.toBase64
import com.avatye.cashblock.base.internal.preference.AccountPreferenceData
import com.avatye.cashblock.base.library.miscellaneous.toDateTimeValue
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import com.avatye.cashblock.base.library.rally.Rally
import com.avatye.cashblock.base.library.rally.request.RallyRequest
import com.avatye.cashblock.base.library.rally.response.RallyFailure
import com.avatye.cashblock.base.library.rally.response.RallyResponse
import com.avatye.cashblock.base.library.rally.response.RallySuccess

internal class ServeTask<T : ServeSuccess>(
    private val blockType: BlockType,
    private val authorization: Authorization,
    private val method: Method,
    private val requestUrl: String,
    private val acceptVersion: String,
    private val argsHeader: HashMap<String, String>? = null,
    private val argsBody: HashMap<String, Any>? = null,
    private val responseClass: Class<T>,
    private val responseCallback: ServeResponse<T>,
    private val watcher: ServeWatcher? = null
) {

    companion object {
        private const val URL = "https://%s.roulette.avatye.com"
        private val baseUrl: String by lazy {
            when (Core.appEnvironment) {
                AppEnvironment.DEV -> URL.format("api-dev")
                AppEnvironment.TEST, AppEnvironment.QA -> URL.format("api-test")
                AppEnvironment.STAGE -> URL.format("api-stage")
                AppEnvironment.LIVE -> URL.format("api")
            }
        }

        fun cancelToQueue(context: Context, tag: String) = Rally.cancelToRequestQueue(context = context, tag = tag)
        fun cancelToAllQueue(context: Context) = Rally.cancelToRequestAllQueue(context = context)
    }

    // region { enums }
    internal enum class Authorization { BASIC, BEARER; }

    internal enum class Method(val value: Int) {
        NONE(-99),
        GET(Request.Method.GET),
        POST(Request.Method.POST),
        PUT(Request.Method.PUT),
        DELETE(Request.Method.DELETE);
    }

    internal enum class Environment(val value: String) {
        DEV("dev"),
        TEST("test"),
        STAGE("stage"),
        QA("qa"),
        LIVE("live");

        companion object {
            fun from(value: String): Environment {
                return when (value.lowercase()) {
                    "dev" -> DEV
                    "test" -> TEST
                    "stage" -> STAGE
                    "qa" -> QA
                    "live" -> LIVE
                    else -> LIVE
                }
            }
        }
    }
    // endregion

    // region # Token
    private val tokenOfBasic: String
        get() = when (Core.isInitialized) {
            true -> "Basic %s".format("${Core.appId}:${Core.appSecret}".toBase64)
            false -> ""
        }

    private val tokenOfBearer: String
        get() = when (Core.isInitialized) {
            true -> "bearer ${AccountPreferenceData.accessToken}"
            false -> ""
        }
    // endregion

    private var tag: String = "Serve"
    fun withTag(tag: String) = apply {
        this.tag = tag
    }

    internal fun execute() {
        if (tag.isEmpty()) {
            throw Exception("ServeTask Tag value is empty. Tag value must not null or empty")
        }

        if (method == Method.NONE) {
            throw Exception("ServeTask Method have not defined. Tag value must not null or empty")
        }

        if (requestUrl.isEmpty()) {
            throw Exception("ServeTask requestUrl value is empty. requestUrl value must not null or empty")
        }

        if (acceptVersion.isEmpty()) {
            throw Exception("ServeTask acceptVersion value is empty. acceptVersion value must not null or empty")
        }

        Rally.addToRequest(
            context = Core.application,
            request = RallyRequest(
                reqTag = tag,
                reqMethod = method.value,
                reqUrl = "$baseUrl/$requestUrl",
                reqHeader = makeHeaderArgs(),
                reqBody = argsBody
            ),
            response = rallyResponse
        )
    }

    private fun makeHeaderArgs(): HashMap<String, String> {
        return hashMapOf<String, String>().apply {
            this["x-device-os"] = "android"
            this["x-device-os-version"] = "${Build.VERSION.SDK_INT}"
            this["x-device-model"] = "${Build.MODEL}:${Build.MANUFACTURER}"
            this["x-app-package"] = Core.appPackageName
            this["x-app-version-code"] = Core.appVersionCode
            this["x-app-version-name"] = Core.appVersionName
            this["Content-Type"] = "application/json"
            this["accept-version"] = acceptVersion
            this["Authorization"] = when (authorization) {
                Authorization.BASIC -> tokenOfBasic
                Authorization.BEARER -> tokenOfBearer
            }
            argsHeader?.let {
                for ((key, value) in it) {
                    this[key] = value
                }
            }
        }
    }

    private val rallyResponse = object : RallyResponse {
        override fun onSuccess(response: RallySuccess) {
            parseSuccess(response)
            watcher?.onPostExecute(success = true)
        }

        override fun onFailure(error: RallyFailure) {
            parseError(error)
            watcher?.onPostExecute(success = false)
        }
    }

    private fun parseSuccess(response: RallySuccess) {
        val responseEntity = responseClass.newInstance()
        try {
            responseEntity.setResponseRawValue(response.body)
            responseCallback.onSuccess(responseEntity)
        } catch (e: Exception) {
            responseCallback.onFailure(failure = ServeFailure(message = e.message ?: "unknown error"))
        }
    }

    private fun parseError(error: RallyFailure) {
        // status Unauthorized
        if (error.statusCode == 401) {
            EventContractor.postUnauthorized(blockType = blockType)
        }
        // status Forbidden
        if (error.statusCode == 403) {
            EventContractor.postForbidden(blockType = blockType)
        }
        // status Inspection
        if (error.statusCode == 503 || error.statusCode == 504) {
            Core.appInspection = makeInspectionEntity(rallyFailure = error)
            EventContractor.postInspection(blockType = blockType)
        }

        responseCallback.onFailure(
            failure = ServeFailure(
                statusCode = error.statusCode,
                serverCode = error.body?.toStringValue("code") ?: "",
                message = error.body?.toStringValue("message") ?: "",
                body = error.body
            )
        )
    }

    private fun makeInspectionEntity(rallyFailure: RallyFailure): AppInspection? {
        var entity: AppInspection? = null
        rallyFailure.body?.let {
            entity = AppInspection(
                blockType = blockType,
                fromDateTime = it.toDateTimeValue("startDateTime"),
                toDateTime = it.toDateTimeValue("endDateTime"),
                message = it.toStringValue("message"),
                link = it.toStringValue("link")
            )
        }
        return entity
    }
}