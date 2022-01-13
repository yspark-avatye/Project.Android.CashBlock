package com.avatye.cashblock.base.component.contract.api

import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.box.BoxAvailableEntity
import com.avatye.cashblock.base.component.domain.entity.box.BoxType
import com.avatye.cashblock.base.component.domain.entity.box.BoxUseEntity
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.internal.server.APIBox
import com.avatye.cashblock.base.internal.server.entity.box.ResBoxAvailable
import com.avatye.cashblock.base.internal.server.entity.box.ResBoxUse
import com.avatye.cashblock.base.internal.server.serve.ServeFailure
import com.avatye.cashblock.base.internal.server.serve.ServeResponse

class BoxApiContractor(private val blockType: BlockType) {

    fun retrieveAvailable(boxType: BoxType, response: (contract: ContractResult<BoxAvailableEntity>) -> Unit) {
        APIBox.getTicketBoxAvailable(
            blockType = blockType,
            boxType = boxType,
            response = object : ServeResponse<ResBoxAvailable> {
                override fun onSuccess(success: ResBoxAvailable) = response(Contract.onSuccess(success.boxAvailable))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }

    fun postUse(boxType: BoxType, response: (contract: ContractResult<BoxUseEntity>) -> Unit) {
        APIBox.postUseBox(
            blockType = blockType,
            boxType = boxType,
            response = object : ServeResponse<ResBoxUse> {
                override fun onSuccess(success: ResBoxUse) = response(Contract.onSuccess(success.boxUse))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }
}