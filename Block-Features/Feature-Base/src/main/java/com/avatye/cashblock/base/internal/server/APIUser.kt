package com.avatye.cashblock.base.internal.server

import com.avatye.cashblock.base.Core
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.user.Profile
import com.avatye.cashblock.base.internal.server.entity.ResVoid
import com.avatye.cashblock.base.internal.server.entity.user.ResLogin
import com.avatye.cashblock.base.internal.server.serve.ServeResponse
import com.avatye.cashblock.base.internal.server.serve.ServeTask

internal object APIUser {
    fun putLogin(blockType: BlockType, profile: Profile, response: ServeResponse<ResLogin>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BASIC,
            method = ServeTask.Method.PUT,
            requestUrl = "user/login",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf(
                "appID" to Core.appId,
                "appUserID" to profile.userId,
                "gender" to profile.gender.value,
                "birthDate" to profile.birthYear.toString()
            ),
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

    fun putUpdateUser(blockType: BlockType, deviceADID: String? = null, gender: Int? = null, birthDate: String? = null, response: ServeResponse<ResVoid>) {
        val bodyArgs = hashMapOf<String, Any>().apply {
            deviceADID?.let {
                this["deviceADID"] = it
            }
            gender?.let {
                this["gender"] = it
            }
            birthDate?.let {
                this["birthDate"]
            }
        }
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.PUT,
            requestUrl = "user",
            acceptVersion = "1.0.0",
            argsBody = bodyArgs,
            responseClass = ResVoid::class.java,
            responseCallback = response
        ).execute()
    }

}