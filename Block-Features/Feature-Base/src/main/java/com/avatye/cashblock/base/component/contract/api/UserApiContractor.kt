package com.avatye.cashblock.base.component.contract.api

import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.user.LoginEntity
import com.avatye.cashblock.base.component.domain.entity.user.Profile
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.internal.server.APIUser
import com.avatye.cashblock.base.internal.server.entity.ResVoid
import com.avatye.cashblock.base.internal.server.entity.user.ResLogin
import com.avatye.cashblock.base.internal.server.serve.ServeFailure
import com.avatye.cashblock.base.internal.server.serve.ServeResponse

class UserApiContractor(private val blockType: BlockType) {
    fun postLogin(profile: Profile, response: (contract: ContractResult<LoginEntity>) -> Unit) {
        APIUser.putLogin(
            blockType = blockType,
            profile = profile,
            response = object : ServeResponse<ResLogin> {
                override fun onSuccess(success: ResLogin) = response(Contract.onSuccess(success.loginEntity))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }

    fun postVerifyAge(birthDate: String, response: (contract: ContractResult<Unit>) -> Unit) {
        APIUser.putVerifyAge(
            blockType = blockType,
            birthDate = birthDate,
            response = object : ServeResponse<ResVoid> {
                override fun onSuccess(success: ResVoid) = response(Contract.onSuccess(Unit))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }

    fun putUser(
        deviceADID: String? = null,
        gender: Int? = null,
        birthDate: String? = null,
        response: (contract: ContractResult<Unit>) -> Unit
    ) {
        APIUser.putUpdateUser(
            blockType = blockType,
            deviceADID = deviceADID,
            gender = gender,
            birthDate = birthDate,
            response = object : ServeResponse<ResVoid> {
                override fun onSuccess(success: ResVoid) = response(Contract.onSuccess(Unit))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            }
        )
    }
}