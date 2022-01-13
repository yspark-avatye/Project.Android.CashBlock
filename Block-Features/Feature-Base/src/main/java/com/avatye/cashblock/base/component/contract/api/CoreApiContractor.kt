package com.avatye.cashblock.base.component.contract.api

import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.setting.SettingEntity
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.internal.server.APICore
import com.avatye.cashblock.base.internal.server.entity.app.ResSettings
import com.avatye.cashblock.base.internal.server.serve.ServeFailure
import com.avatye.cashblock.base.internal.server.serve.ServeResponse

class CoreApiContractor(private val blockType: BlockType) {

    fun retrieveAppSettings(keys: List<String>? = null, response: (contract: ContractResult<SettingEntity>) -> Unit) {
        APICore.getAppSettings(
            blockType = blockType,
            keys = keys,
            response = object : ServeResponse<ResSettings> {
                override fun onSuccess(success: ResSettings) = response(Contract.onSuccess(success.settings))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }

    fun postEventLog(eventKey: String, eventParam: HashMap<String, Any>? = null) {
        APICore.postEventLog(blockType = blockType, eventKey = eventKey, eventParam = eventParam)
    }
}