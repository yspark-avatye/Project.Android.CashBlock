package com.avatye.cashblock.base.component.contract.data

import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.component.contract.AccountContract
import com.avatye.cashblock.base.component.domain.entity.mission.MissionEntity
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.internal.server.APIMission
import com.avatye.cashblock.base.internal.server.entity.mission.ResMission
import com.avatye.cashblock.base.internal.server.serve.IServeToken
import com.avatye.cashblock.base.internal.server.serve.ServeFailure
import com.avatye.cashblock.base.internal.server.serve.ServeResponse

class MissionDataContract(private val blockCode: BlockCode) {
    private val appId = blockCode.blockId

    private val tokenizer = object : IServeToken {
        override fun makeBasicToken() = blockCode.basicToken
        override fun makeBearerToken() = AccountContract.accessToken
    }

    fun retrieveMission(missionId: String, response: (contract: ContractResult<MissionEntity>) -> Unit) {
        APIMission.getUser(appId = appId, tokenizer = tokenizer, missionId = missionId, response = object : ServeResponse<ResMission> {
            override fun onSuccess(success: ResMission) = response(Contract.onSuccess(success.missionEntity))
            override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
        })
    }

    fun postAction(missionID: String, actionValue: Int, response: (contract: ContractResult<MissionEntity>) -> Unit) {
        APIMission.postAction(appId = appId, tokenizer = tokenizer, missionId = missionID, actionValue = actionValue, response = object : ServeResponse<ResMission> {
            override fun onSuccess(success: ResMission) = response(Contract.onSuccess(success.missionEntity))
            override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
        })
    }
}