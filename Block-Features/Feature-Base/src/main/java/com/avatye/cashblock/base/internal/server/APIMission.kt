package com.avatye.cashblock.base.internal.server

import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.internal.server.entity.mission.ResMission
import com.avatye.cashblock.base.internal.server.serve.ServeResponse
import com.avatye.cashblock.base.internal.server.serve.ServeTask

internal object APIMission {
    fun getUser(blockType: BlockType, missionId: String, response: ServeResponse<ResMission>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.GET,
            requestUrl = "mission/user",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf("missionID" to missionId),
            responseClass = ResMission::class.java,
            responseCallback = response
        ).execute()
    }

    fun postAction(blockType: BlockType, missionId: String, actionValue: Int, response: ServeResponse<ResMission>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
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