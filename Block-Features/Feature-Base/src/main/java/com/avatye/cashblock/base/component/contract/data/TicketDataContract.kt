package com.avatye.cashblock.base.component.contract.data

import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.component.contract.AccountContract
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
import com.avatye.cashblock.base.internal.server.serve.IServeToken
import com.avatye.cashblock.base.internal.server.serve.ServeFailure
import com.avatye.cashblock.base.internal.server.serve.ServeResponse

class TicketDataContract(private val blockCode: BlockCode) {
    private val appId = blockCode.blockId

    private val tokenizer = object : IServeToken {
        override fun makeBasicToken() = blockCode.basicToken
        override fun makeBearerToken() = AccountContract.accessToken
    }

    fun retrieveCondition(ticketType: TicketType, response: (contract: ContractResult<TicketCountEntity>) -> Unit) {
        APITicket.getTicketCount(
            blockCode = blockCode,
            tokenizer = tokenizer,
            ticketType = ticketType,
            response = object : ServeResponse<ResTicketCount> {
                override fun onSuccess(success: ResTicketCount) = response(Contract.onSuccess(success.ticketCountEntity))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }

    fun retrieveBalance(response: (contract: ContractResult<TicketBalanceEntity>) -> Unit) {
        APITicket.getTicketBalance(
            blockCode = blockCode,
            tokenizer = tokenizer,
            response = object : ServeResponse<ResTicketBalance> {
                override fun onSuccess(success: ResTicketBalance) = response(Contract.onSuccess(success.ticketBalanceEntity))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }

    fun postTransaction(ticketType: TicketType, response: (contract: ContractResult<TicketRequestEntity>) -> Unit) {
        APITicket.postTicketRequest(
            blockCode = blockCode,
            tokenizer = tokenizer,
            ticketType = ticketType,
            response = object : ServeResponse<ResTicketRequest> {
                override fun onSuccess(success: ResTicketRequest) = response(Contract.onSuccess(success.ticketRequestEntity))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }

    fun postTicketing(ticketType: TicketType, transactionId: String, response: (Contract: ContractResult<TicketBalanceEntity>) -> Unit) {
        APITicket.putTicketGive(
            blockCode = blockCode,
            tokenizer = tokenizer,
            ticketType = ticketType,
            transactionId = transactionId,
            response = object : ServeResponse<ResTicketGive> {
                override fun onSuccess(success: ResTicketGive) = response(Contract.onSuccess(success.ticketBalanceEntity))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }
}