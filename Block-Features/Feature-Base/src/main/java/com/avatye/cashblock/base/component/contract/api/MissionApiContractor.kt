package com.avatye.cashblock.base.component.contract.api

import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.mission.MissionEntity
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.internal.server.APIMission
import com.avatye.cashblock.base.internal.server.entity.mission.ResMission
import com.avatye.cashblock.base.internal.server.serve.ServeFailure
import com.avatye.cashblock.base.internal.server.serve.ServeResponse

class MissionApiContractor(private val blockType: BlockType) {

    fun retrieveMission(missionId: String, response: (contract: ContractResult<MissionEntity>) -> Unit) {
        APIMission.getUser(
            blockType = blockType,
            missionId = missionId,
            response = object : ServeResponse<ResMission> {
                override fun onSuccess(success: ResMission) = response(Contract.onSuccess(success.missionEntity))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }

    fun postAction(missionID: String, actionValue: Int, response: (contract: ContractResult<MissionEntity>) -> Unit) {
        APIMission.postAction(
            blockType = blockType,
            missionId = missionID,
            actionValue = actionValue,
            response = object : ServeResponse<ResMission> {
                override fun onSuccess(success: ResMission) = response(Contract.onSuccess(success.missionEntity))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }
}