package com.avatye.cashblock.base.internal.server

import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.ticket.TicketType
import com.avatye.cashblock.base.internal.server.entity.ticket.ResTicketBalance
import com.avatye.cashblock.base.internal.server.entity.ticket.ResTicketCount
import com.avatye.cashblock.base.internal.server.entity.ticket.ResTicketGive
import com.avatye.cashblock.base.internal.server.entity.ticket.ResTicketRequest
import com.avatye.cashblock.base.internal.server.serve.ServeResponse
import com.avatye.cashblock.base.internal.server.serve.ServeTask

internal object APITicket {
    /**
     * get 'ticket' count
     */
    fun getTicketCount(blockType: BlockType, ticketType: TicketType, response: ServeResponse<ResTicketCount>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
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
    fun getTicketBalance(blockType: BlockType, response: ServeResponse<ResTicketBalance>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
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
    fun postTicketRequest(blockType: BlockType, ticketType: TicketType, response: ServeResponse<ResTicketRequest>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
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
    fun putTicketGive(blockType: BlockType, ticketType: TicketType, transactionId: String, response: ServeResponse<ResTicketGive>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
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