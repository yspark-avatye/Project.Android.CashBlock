package com.avatye.cashblock.base.internal.server

import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.internal.server.entity.support.ResNotice
import com.avatye.cashblock.base.internal.server.entity.support.ResNoticeList
import com.avatye.cashblock.base.internal.server.entity.support.ResPopupNotice
import com.avatye.cashblock.base.internal.server.serve.IServeToken
import com.avatye.cashblock.base.internal.server.serve.ServeResponse
import com.avatye.cashblock.base.internal.server.serve.ServeTask

object APISupport {
    fun getNoticeList(blockCode: BlockCode, tokenizer: IServeToken, offset: Int = 0, limit: Int = 50, response: ServeResponse<ResNoticeList>) {
        ServeTask(
            blockCode = blockCode,
            authorization = ServeTask.Authorization.BASIC,
            tokenizer = tokenizer,
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

    fun getNoticeView(blockCode: BlockCode, tokenizer: IServeToken, noticeId: String, response: ServeResponse<ResNotice>) {
        ServeTask(
            blockCode = blockCode,
            authorization = ServeTask.Authorization.BASIC,
            tokenizer = tokenizer,
            method = ServeTask.Method.GET,
            requestUrl = "support/notice",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf("noticeID" to noticeId),
            responseClass = ResNotice::class.java,
            responseCallback = response
        ).execute()
    }

    fun getPopups(blockCode: BlockCode, tokenizer: IServeToken, response: ServeResponse<ResPopupNotice>) {
        ServeTask(
            blockCode = blockCode,
            authorization = ServeTask.Authorization.BASIC,
            tokenizer = tokenizer,
            method = ServeTask.Method.GET,
            requestUrl = "support/popups",
            acceptVersion = "1.0.0",
            responseClass = ResPopupNotice::class.java,
            responseCallback = response
        ).execute()
    }

}