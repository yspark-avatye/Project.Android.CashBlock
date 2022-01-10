package com.avatye.cashblock.base.internal.server

import com.avatye.cashblock.base.internal.server.entity.ResVoid
import com.avatye.cashblock.base.internal.server.entity.user.ResLogin
import com.avatye.cashblock.base.internal.server.serve.IServeToken
import com.avatye.cashblock.base.internal.server.serve.ServeTask
import com.avatye.cashblock.base.internal.server.serve.ServeResponse

object APIUser {
    fun putLogin(appId: String, tokenizer: IServeToken, appUserId: String, response: ServeResponse<ResLogin>) {
        ServeTask(
            appId = appId,
            authorization = ServeTask.Authorization.BASIC,
            tokenizer = tokenizer,
            method = ServeTask.Method.PUT,
            requestUrl = "user/login",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf("appUserID" to appUserId),
            responseClass = ResLogin::class.java,
            responseCallback = response
        ).execute()
    }

    fun putVerifyAge(appId: String, tokenizer: IServeToken, birthDate: String, response: ServeResponse<ResVoid>) {
        ServeTask(
            appId = appId,
            authorization = ServeTask.Authorization.BEARER,
            tokenizer = tokenizer,
            method = ServeTask.Method.POST,
            requestUrl = "user/age",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf("birthDate" to birthDate),
            responseClass = ResVoid::class.java,
            responseCallback = response
        ).execute()
    }
}