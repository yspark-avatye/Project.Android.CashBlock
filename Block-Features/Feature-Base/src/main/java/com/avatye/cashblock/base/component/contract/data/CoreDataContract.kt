package com.avatye.cashblock.base.component.contract.data

import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.component.contract.AccountContract
import com.avatye.cashblock.base.component.domain.entity.setting.SettingEntity
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.internal.server.APICore
import com.avatye.cashblock.base.internal.server.entity.app.ResSettings
import com.avatye.cashblock.base.internal.server.serve.IServeToken
import com.avatye.cashblock.base.internal.server.serve.ServeFailure
import com.avatye.cashblock.base.internal.server.serve.ServeResponse

class CoreDataContract(private val blockCode: BlockCode) {
    private val appId = blockCode.blockId

    private val tokenizer = object : IServeToken {
        override fun makeBasicToken() = blockCode.basicToken
        override fun makeBearerToken() = AccountContract.accessToken
    }

    fun retrieveAppSettings(keys: List<String>? = null, response: (contract: ContractResult<SettingEntity>) -> Unit) {
        APICore.getAppSettings(appId = appId, tokenizer = tokenizer, keys = keys, response = object : ServeResponse<ResSettings> {
            override fun onSuccess(success: ResSettings) = response(Contract.onSuccess(success.settings))
            override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
        })
    }

    fun postEventLog(eventKey: String, eventParam: HashMap<String, Any>? = null) {
        APICore.postEventLog(appId = appId, tokenizer = tokenizer, eventKey = eventKey, eventParam = eventParam)
    }
}