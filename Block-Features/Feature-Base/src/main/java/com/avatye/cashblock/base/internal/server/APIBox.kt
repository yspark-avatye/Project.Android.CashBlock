package com.avatye.cashblock.base.internal.server

import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.component.domain.entity.box.BoxType
import com.avatye.cashblock.base.internal.server.entity.box.ResBoxAvailable
import com.avatye.cashblock.base.internal.server.entity.box.ResBoxUse
import com.avatye.cashblock.base.internal.server.serve.IServeToken
import com.avatye.cashblock.base.internal.server.serve.ServeResponse
import com.avatye.cashblock.base.internal.server.serve.ServeTask

object APIBox {
    fun getTicketBoxAvailable(blockCode: BlockCode, tokenizer: IServeToken, boxType: BoxType, response: ServeResponse<ResBoxAvailable>) {
        ServeTask(
            blockCode = blockCode,
            authorization = ServeTask.Authorization.BEARER,
            tokenizer = tokenizer,
            method = ServeTask.Method.GET,
            requestUrl = "box/user",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf("boxType" to boxType.value),
            responseClass = ResBoxAvailable::class.java,
            responseCallback = response
        ).execute()
    }

    fun postUseBox(blockCode: BlockCode, tokenizer: IServeToken, boxType: BoxType, response: ServeResponse<ResBoxUse>) {
        ServeTask(
            blockCode = blockCode,
            authorization = ServeTask.Authorization.BEARER,
            tokenizer = tokenizer,
            method = ServeTask.Method.POST,
            requestUrl = "box/use",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf("boxType" to boxType.value),
            responseClass = ResBoxUse::class.java,
            responseCallback = response
        ).execute()
    }
}