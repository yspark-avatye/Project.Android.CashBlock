package com.avatye.cashblock.base.internal.server

import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.box.BoxType
import com.avatye.cashblock.base.internal.server.entity.box.ResBoxAvailable
import com.avatye.cashblock.base.internal.server.entity.box.ResBoxUse
import com.avatye.cashblock.base.internal.server.serve.ServeResponse
import com.avatye.cashblock.base.internal.server.serve.ServeTask

internal object APIBox {
    fun getTicketBoxAvailable(blockType: BlockType, boxType: BoxType, response: ServeResponse<ResBoxAvailable>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.GET,
            requestUrl = "box/user",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf("boxType" to boxType.value),
            responseClass = ResBoxAvailable::class.java,
            responseCallback = response
        ).execute()
    }

    fun postUseBox(blockType: BlockType, boxType: BoxType, response: ServeResponse<ResBoxUse>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.POST,
            requestUrl = "box/use",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf("boxType" to boxType.value),
            responseClass = ResBoxUse::class.java,
            responseCallback = response
        ).execute()
    }
}