package com.avatye.cashblock.base.component.contract.data

import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.component.contract.AccountContract
import com.avatye.cashblock.base.component.domain.entity.support.NoticeEntity
import com.avatye.cashblock.base.component.domain.entity.support.PopupNoticeEntity
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.internal.server.APISupport
import com.avatye.cashblock.base.internal.server.entity.support.ResNotice
import com.avatye.cashblock.base.internal.server.entity.support.ResNoticeList
import com.avatye.cashblock.base.internal.server.entity.support.ResPopupNotice
import com.avatye.cashblock.base.internal.server.serve.IServeToken
import com.avatye.cashblock.base.internal.server.serve.ServeFailure
import com.avatye.cashblock.base.internal.server.serve.ServeResponse

class SupportDataContract(private val blockCode: BlockCode) {
    private val appId = blockCode.blockId

    private val tokenizer = object : IServeToken {
        override fun makeBasicToken() = blockCode.basicToken
        override fun makeBearerToken() = AccountContract.accessToken
    }

    fun retrieveNoticeList(offset: Int = 0, limit: Int = 50, response: (contract: ContractResult<MutableList<NoticeEntity>>) -> Unit) {
        APISupport.getNoticeList(appId = appId, tokenizer = tokenizer, offset = offset, limit = limit, response = object : ServeResponse<ResNoticeList> {
            override fun onSuccess(success: ResNoticeList) = response(Contract.onSuccess(success = success.noticeEntities))
            override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
        })
    }

    fun retrieveNoticeView(noticeId: String, response: (contract: ContractResult<NoticeEntity>) -> Unit) {
        APISupport.getNoticeView(appId = appId, tokenizer = tokenizer, noticeId = noticeId, response = object : ServeResponse<ResNotice> {
            override fun onSuccess(success: ResNotice) = response(Contract.onSuccess(success = success.noticeEntity))
            override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
        })
    }

    fun retrievePopups(response: (contract: ContractResult<MutableList<PopupNoticeEntity>>) -> Unit) {
        APISupport.getPopups(appId = appId, tokenizer = tokenizer, response = object : ServeResponse<ResPopupNotice> {
            override fun onSuccess(success: ResPopupNotice) = response(Contract.onSuccess(success.popupNoticeEntities))
            override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
        })
    }
}