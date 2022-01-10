package com.avatye.cashblock.base.internal.server

import com.avatye.cashblock.base.internal.server.entity.app.ResPublisherInitialize
import com.avatye.cashblock.base.internal.server.serve.ServeResponse
import com.avatye.cashblock.base.internal.server.serve.ServeTask

object APIPublisher {
    fun getInitialize(
        publisherID: String,
        publisherAppKey: String,
        publisherAppName: String,
        publisherAAID: String,
        response: ServeResponse<ResPublisherInitialize>
    ) {
        ServeTask(
            authorization = ServeTask.Authorization.BASIC,
            method = ServeTask.Method.GET,
            requestUrl = "app/initialize",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf<String, Any>().apply {
                this["publisherID"] = publisherID
                this["publisherAppKey"] = publisherAppKey
                this["publisherAppName"] = publisherAppName
                this["deviceADID"] = publisherAAID
            },
            responseClass = ResPublisherInitialize::class.java,
            responseCallback = response
        ).execute()
    }
}