package com.avatye.cashblock.base.internal.server

import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.component.domain.entity.ticket.TicketType
import com.avatye.cashblock.base.internal.server.entity.ticket.ResTicketBalance
import com.avatye.cashblock.base.internal.server.entity.ticket.ResTicketCount
import com.avatye.cashblock.base.internal.server.entity.ticket.ResTicketGive
import com.avatye.cashblock.base.internal.server.entity.ticket.ResTicketRequest
import com.avatye.cashblock.base.internal.server.serve.IServeToken
import com.avatye.cashblock.base.internal.server.serve.ServeResponse
import com.avatye.cashblock.base.internal.server.serve.ServeTask

object APITicket {
    /**
     * get 'ticket' count
     */
    fun getTicketCount(blockCode: BlockCode, tokenizer: IServeToken, ticketType: TicketType, response: ServeResponse<ResTicketCount>) {
        ServeTask(
            blockCode = blockCode,
            authorization = ServeTask.Authorization.BEARER,
            tokenizer = tokenizer,
            method = ServeTask.Method.GET,
            requestUrl = "user/ticket/count",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf("giveType" to ticketType.value),
            responseClass = ResTicketCount::class.java,
            responseCallback = response
        ).execute()
    }

    /**
     * get 'ticket' balance
     */
    fun getTicketBalance(blockCode: BlockCode, tokenizer: IServeToken, response: ServeResponse<ResTicketBalance>) {
        ServeTask(
            blockCode = blockCode,
            authorization = ServeTask.Authorization.BEARER,
            tokenizer = tokenizer,
            method = ServeTask.Method.GET,
            requestUrl = "user/ticket/balance",
            acceptVersion = "1.0.0",
            responseClass = ResTicketBalance::class.java,
            responseCallback = response
        ).execute()
    }

    /**
     * post 'ticket' request (transaction request)
     */
    fun postTicketRequest(blockCode: BlockCode, tokenizer: IServeToken, ticketType: TicketType, response: ServeResponse<ResTicketRequest>) {
        ServeTask(
            blockCode = blockCode,
            authorization = ServeTask.Authorization.BEARER,
            tokenizer = tokenizer,
            method = ServeTask.Method.POST,
            requestUrl = "user/ticket/request",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf("giveType" to ticketType.value),
            responseClass = ResTicketRequest::class.java,
            responseCallback = response
        ).execute()
    }

    /**
     * put 'ticket' give (transaction complete)
     */
    fun putTicketGive(blockCode: BlockCode, tokenizer: IServeToken, ticketType: TicketType, transactionId: String, response: ServeResponse<ResTicketGive>) {
        ServeTask(
            blockCode = blockCode,
            authorization = ServeTask.Authorization.BEARER,
            tokenizer = tokenizer,
            method = ServeTask.Method.PUT,
            requestUrl = "user/ticket/give",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf(
                "giveType" to ticketType.value,
                "ticketTransactionID" to transactionId
            ),
            responseClass = ResTicketGive::class.java,
            responseCallback = response
        ).execute()
    }

}