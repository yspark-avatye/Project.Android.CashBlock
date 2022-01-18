package com.avatye.cashblock.base.internal.server

import com.avatye.cashblock.base.Core
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.internal.server.entity.support.ResNotice
import com.avatye.cashblock.base.internal.server.entity.support.ResNoticeList
import com.avatye.cashblock.base.internal.server.entity.support.ResPopupNotice
import com.avatye.cashblock.base.internal.server.serve.ServeResponse
import com.avatye.cashblock.base.internal.server.serve.ServeTask

internal object APISupport {
    fun getNoticeList(blockType: BlockType, offset: Int = 0, limit: Int = 50, response: ServeResponse<ResNoticeList>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BASIC,
            method = ServeTask.Method.GET,
            requestUrl = "support/notices",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf(
                "offset" to offset,
                "limit" to limit
            ),
            responseClass = ResNoticeList::class.java,
            responseCallback = response
        ).execute()
    }

    fun getNoticeView(blockType: BlockType, noticeId: String, response: ServeResponse<ResNotice>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BASIC,
            method = ServeTask.Method.GET,
            requestUrl = "support/notice",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf("noticeID" to noticeId),
            responseClass = ResNotice::class.java,
            responseCallback = response
        ).execute()
    }

    fun getPopups(blockType: BlockType, response: ServeResponse<ResPopupNotice>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BASIC,
            method = ServeTask.Method.GET,
            requestUrl = "support/popups",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf("appID" to Core.appId),
            responseClass = ResPopupNotice::class.java,
            responseCallback = response
        ).execute()
    }

}