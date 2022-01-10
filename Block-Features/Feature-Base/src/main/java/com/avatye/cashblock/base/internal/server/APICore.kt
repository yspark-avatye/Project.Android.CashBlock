package com.avatye.cashblock.base.internal.server

import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.internal.server.entity.ResVoid
import com.avatye.cashblock.base.internal.server.entity.app.ResSettings
import com.avatye.cashblock.base.internal.server.serve.IServeToken
import com.avatye.cashblock.base.internal.server.serve.ServeFailure
import com.avatye.cashblock.base.internal.server.serve.ServeResponse
import com.avatye.cashblock.base.internal.server.serve.ServeTask
import org.json.JSONObject

object APICore {
    fun postEventLog(blockCode: BlockCode, tokenizer: IServeToken, eventKey: String, eventParam: HashMap<String, Any>? = null) {
        ServeTask(
            blockCode = blockCode,
            authorization = ServeTask.Authorization.BEARER,
            tokenizer = tokenizer,
            method = ServeTask.Method.POST,
            requestUrl = "log/event",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf(
                "eventKey" to eventKey,
                "eventParam" to makeParams(eventParam)
            ),
            responseClass = ResVoid::class.java,
            responseCallback = object : ServeResponse<ResVoid> {
                override fun onSuccess(success: ResVoid) {}
                override fun onFailure(failure: ServeFailure) {}
            }
        ).execute()
    }

    fun getAppSettings(blockCode: BlockCode, tokenizer: IServeToken, keys: List<String>? = null, response: ServeResponse<ResSettings>) {
        ServeTask(
            blockCode = blockCode,
            authorization = ServeTask.Authorization.BASIC,
            tokenizer = tokenizer,
            method = ServeTask.Method.GET,
            requestUrl = "app/settings",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf<String, Any>().apply {
                if (keys?.isNotEmpty() == true) {
                    this["keys"] = keys.joinToString(separator = ",")
                }
            },
            responseClass = ResSettings::class.java,
            responseCallback = response
        ).execute()
    }

    private fun makeParams(eventParam: HashMap<String, Any>? = null): String {
        var params = "{}"
        try {
            eventParam?.let {
                val jsonObject = JSONObject()
                for ((key, value) in it) {
                    jsonObject.put(key, value)
                }
                params = jsonObject.toString()
            }
        } catch (e: Exception) {
            params = "{}"
        }
        return params
    }
}