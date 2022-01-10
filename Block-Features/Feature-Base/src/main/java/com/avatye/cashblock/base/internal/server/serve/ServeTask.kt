package com.avatye.cashblock.base.internal.server.serve

import android.content.Context
import android.os.Build
import com.android.volley.Request
import com.avatye.cashblock.base.FeatureCore
import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.component.contract.EventBusContract
import com.avatye.cashblock.base.component.domain.entity.app.AppInspection
import com.avatye.cashblock.base.library.miscellaneous.toDateTimeValue
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import com.avatye.cashblock.base.library.rally.Rally
import com.avatye.cashblock.base.library.rally.request.RallyRequest
import com.avatye.cashblock.base.library.rally.response.RallyFailure
import com.avatye.cashblock.base.library.rally.response.RallyResponse
import com.avatye.cashblock.base.library.rally.response.RallySuccess

internal class ServeTask<T : ServeSuccess>(
    private val blockCode: BlockCode,
    private val authorization: Authorization,
    private val tokenizer: IServeToken? = null,
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
            when (FeatureCore.appEnvironment) {
                ServeEnvironment.DEV -> URL.format("api-dev")
                ServeEnvironment.TEST, ServeEnvironment.QA -> URL.format("api-test")
                ServeEnvironment.STAGE -> URL.format("api-stage")
                ServeEnvironment.LIVE -> URL.format("api")
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
            context = FeatureCore.application,
            request = RallyRequest(
                reqTag = tag,
                reqMethod = method.value,
                reqUrl = "$baseUrl/$requestUrl",
                reqHeader = makeHeaderArgs(),
                reqBody = makeBodyArgs()
            ),
            response = rallyResponse
        )
    }

    private fun makeHeaderArgs(): HashMap<String, String> {
        return hashMapOf<String, String>().apply {
            this["x-device-os"] = "android"
            this["x-device-os-version"] = "${Build.VERSION.SDK_INT}"
            this["x-device-model"] = "${Build.MODEL}:${Build.MANUFACTURER}"
            this["x-app-package"] = FeatureCore.appPackageName
            this["x-app-version-code"] = FeatureCore.appVersionCode
            this["x-app-version-name"] = FeatureCore.appVersionName
            this["x-app-service-name"] = FeatureCore.appServiceName
            this["Content-Type"] = "application/json"
            this["accept-version"] = acceptVersion
            tokenizer?.let {
                this["Authorization"] = when (authorization) {
                    Authorization.BASIC -> "Basic ${it.makeBasicToken()}"
                    Authorization.BEARER -> "bearer ${it.makeBearerToken()}"
                }
            }
            argsHeader?.let {
                for ((key, value) in it) {
                    this[key] = value
                }
            }
        }
    }

    private fun makeBodyArgs() = (argsBody ?: HashMap<String, Any>()).apply {
        if (!this.containsKey("appID")) {
            this["appID"] = blockCode.blockId
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
            EventBusContract.postUnauthorized(blockType = this.blockCode.blockType)
        }
        // status Forbidden
        if (error.statusCode == 403) {
            EventBusContract.postForbidden(blockType = this.blockCode.blockType)
        }
        // status Inspection
        if (error.statusCode == 503 || error.statusCode == 504) {
            FeatureCore.appInspection = makeInspectionEntity(rallyFailure = error)
            EventBusContract.postInspection(blockType = this.blockCode.blockType)
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
                blockType = this.blockCode.blockType,
                fromDateTime = it.toDateTimeValue("startDateTime"),
                toDateTime = it.toDateTimeValue("endDateTime"),
                message = it.toStringValue("message"),
                link = it.toStringValue("link")
            )
        }
        return entity
    }
}