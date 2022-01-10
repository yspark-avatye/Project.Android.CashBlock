package com.avatye.cashblock.base.component.contract.data

import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.component.contract.AccountContract
import com.avatye.cashblock.base.component.domain.entity.user.LoginEntity
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.internal.server.APIUser
import com.avatye.cashblock.base.internal.server.entity.ResVoid
import com.avatye.cashblock.base.internal.server.entity.user.ResLogin
import com.avatye.cashblock.base.internal.server.serve.IServeToken
import com.avatye.cashblock.base.internal.server.serve.ServeFailure
import com.avatye.cashblock.base.internal.server.serve.ServeResponse

class UserDataContract(private val blockCode: BlockCode) {
    private val appId = blockCode.blockId

    private val tokenizer = object : IServeToken {
        override fun makeBasicToken() = blockCode.basicToken
        override fun makeBearerToken() = AccountContract.accessToken
    }

    fun postLogin(appUserId: String, response: (contract: ContractResult<LoginEntity>) -> Unit) {
        APIUser.putLogin(appId = appId, tokenizer = tokenizer, appUserId = appUserId, response = object : ServeResponse<ResLogin> {
            override fun onSuccess(success: ResLogin) = response(Contract.onSuccess(success.loginEntity))
            override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
        })
    }

    fun postVerifyAge(birthDate: String, response: (contract: ContractResult<Unit>) -> Unit) {
        APIUser.putVerifyAge(appId = appId, tokenizer = tokenizer, birthDate = birthDate, response = object : ServeResponse<ResVoid> {
            override fun onSuccess(success: ResVoid) = response(Contract.onSuccess(Unit))
            override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
        })
    }
}