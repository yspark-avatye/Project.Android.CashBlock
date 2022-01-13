package com.avatye.cashblock.base.component.contract.api

import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.ticket.TicketBalanceEntity
import com.avatye.cashblock.base.component.domain.entity.ticket.TicketCountEntity
import com.avatye.cashblock.base.component.domain.entity.ticket.TicketRequestEntity
import com.avatye.cashblock.base.component.domain.entity.ticket.TicketType
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.internal.server.APITicket
import com.avatye.cashblock.base.internal.server.entity.ticket.ResTicketBalance
import com.avatye.cashblock.base.internal.server.entity.ticket.ResTicketCount
import com.avatye.cashblock.base.internal.server.entity.ticket.ResTicketGive
import com.avatye.cashblock.base.internal.server.entity.ticket.ResTicketRequest
import com.avatye.cashblock.base.internal.server.serve.ServeFailure
import com.avatye.cashblock.base.internal.server.serve.ServeResponse

class TicketApiContractor(private val blockType: BlockType) {
    fun retrieveCondition(ticketType: TicketType, response: (contract: ContractResult<TicketCountEntity>) -> Unit) {
        APITicket.getTicketCount(
            blockType = blockType,
            ticketType = ticketType,
            response = object : ServeResponse<ResTicketCount> {
                override fun onSuccess(success: ResTicketCount) = response(Contract.onSuccess(success.ticketCountEntity))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }

    fun retrieveBalance(response: (contract: ContractResult<TicketBalanceEntity>) -> Unit) {
        APITicket.getTicketBalance(
            blockType = blockType,
            response = object : ServeResponse<ResTicketBalance> {
                override fun onSuccess(success: ResTicketBalance) = response(Contract.onSuccess(success.ticketBalanceEntity))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }

    fun postTransaction(ticketType: TicketType, response: (contract: ContractResult<TicketRequestEntity>) -> Unit) {
        APITicket.postTicketRequest(
            blockType = blockType,
            ticketType = ticketType,
            response = object : ServeResponse<ResTicketRequest> {
                override fun onSuccess(success: ResTicketRequest) = response(Contract.onSuccess(success.ticketRequestEntity))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }

    fun postTicketing(ticketType: TicketType, transactionId: String, response: (Contract: ContractResult<TicketBalanceEntity>) -> Unit) {
        APITicket.putTicketGive(
            blockType = blockType,
            ticketType = ticketType,
            transactionId = transactionId,
            response = object : ServeResponse<ResTicketGive> {
                override fun onSuccess(success: ResTicketGive) = response(Contract.onSuccess(success.ticketBalanceEntity))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }
}