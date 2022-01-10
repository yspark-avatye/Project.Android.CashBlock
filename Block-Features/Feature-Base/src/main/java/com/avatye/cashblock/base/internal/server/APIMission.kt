package com.avatye.cashblock.base.internal.server

import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.internal.server.entity.mission.ResMission
import com.avatye.cashblock.base.internal.server.serve.IServeToken
import com.avatye.cashblock.base.internal.server.serve.ServeResponse
import com.avatye.cashblock.base.internal.server.serve.ServeTask

object APIMission {
    fun getUser(blockCode: BlockCode, tokenizer: IServeToken, missionId: String, response: ServeResponse<ResMission>) {
        ServeTask(
            blockCode = blockCode,
            authorization = ServeTask.Authorization.BEARER,
            tokenizer = tokenizer,
            method = ServeTask.Method.GET,
            requestUrl = "mission/user",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf("missionID" to missionId),
            responseClass = ResMission::class.java,
            responseCallback = response
        ).execute()
    }

    fun postAction(blockCode: BlockCode, tokenizer: IServeToken, missionId: String, actionValue: Int, response: ServeResponse<ResMission>) {
        ServeTask(
            blockCode = blockCode,
            authorization = ServeTask.Authorization.BEARER,
            tokenizer = tokenizer,
            method = ServeTask.Method.POST,
            requestUrl = "mission/action",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf(
                "missionID" to missionId,
                "actionValue" to actionValue
            ),
            responseClass = ResMission::class.java,
            responseCallback = response
        ).execute()
    }

}