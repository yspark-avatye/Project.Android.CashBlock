package com.avatye.cashblock.base.internal.server

import com.avatye.cashblock.base.Core
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.internal.server.entity.ResVoid
import com.avatye.cashblock.base.internal.server.entity.app.ResSettings
import com.avatye.cashblock.base.internal.server.serve.ServeFailure
import com.avatye.cashblock.base.internal.server.serve.ServeResponse
import com.avatye.cashblock.base.internal.server.serve.ServeTask
import org.json.JSONObject

internal object APICore {
    fun postEventLog(blockType: BlockType, eventKey: String, eventParam: HashMap<String, Any>? = null) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.POST,
            requestUrl = "log/event",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf(
                "appID" to Core.appId,
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

    fun getAppSettings(blockType: BlockType, keys: List<String>? = null, response: ServeResponse<ResSettings>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BASIC,
            method = ServeTask.Method.GET,
            requestUrl = "app/settings",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf<String, Any>().apply {
                this["appID"] = Core.appId
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