package com.avatye.cashblock.base.internal.server

import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.internal.server.entity.ResVoid
import com.avatye.cashblock.base.internal.server.entity.user.ResLogin
import com.avatye.cashblock.base.internal.server.serve.IServeToken
import com.avatye.cashblock.base.internal.server.serve.ServeResponse
import com.avatye.cashblock.base.internal.server.serve.ServeTask

object APIUser {
    fun putLogin(blockCode: BlockCode, tokenizer: IServeToken, appUserId: String, response: ServeResponse<ResLogin>) {
        ServeTask(
            blockCode = blockCode,
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

    fun putVerifyAge(blockCode: BlockCode, tokenizer: IServeToken, birthDate: String, response: ServeResponse<ResVoid>) {
        ServeTask(
            blockCode = blockCode,
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