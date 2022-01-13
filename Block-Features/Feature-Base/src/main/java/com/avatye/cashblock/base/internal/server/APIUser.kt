package com.avatye.cashblock.base.internal.server

import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.internal.server.entity.ResVoid
import com.avatye.cashblock.base.internal.server.entity.user.ResLogin
import com.avatye.cashblock.base.internal.server.serve.ServeResponse
import com.avatye.cashblock.base.internal.server.serve.ServeTask

internal object APIUser {
    fun putLogin(blockType: BlockType, appUserId: String, response: ServeResponse<ResLogin>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BASIC,
            method = ServeTask.Method.PUT,
            requestUrl = "user/login",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf("appUserID" to appUserId),
            responseClass = ResLogin::class.java,
            responseCallback = response
        ).execute()
    }

    fun putVerifyAge(blockType: BlockType, birthDate: String, response: ServeResponse<ResVoid>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.POST,
            requestUrl = "user/age",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf("birthDate" to birthDate),
            responseClass = ResVoid::class.java,
            responseCallback = response
        ).execute()
    }
}