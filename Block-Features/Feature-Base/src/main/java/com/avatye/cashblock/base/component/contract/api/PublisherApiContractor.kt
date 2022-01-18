package com.avatye.cashblock.base.component.contract.api

import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.app.PublisherInitializeEntity
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.internal.server.APIPublisher
import com.avatye.cashblock.base.internal.server.entity.app.ResPublisherInitialize
import com.avatye.cashblock.base.internal.server.serve.ServeFailure
import com.avatye.cashblock.base.internal.server.serve.ServeResponse

class PublisherApiContractor() {
    // publisher not use appid
    fun retrieveInitialize(
        blockType: BlockType,
        publisherID: String,
        publisherAppKey: String,
        publisherAppName: String,
        publisherAAID: String,
        response: (contract: ContractResult<PublisherInitializeEntity>) -> Unit
    ) {
        APIPublisher.getInitialize(
            blockType = blockType,
            publisherID = publisherID,
            publisherAppKey = publisherAppKey,
            publisherAppName = publisherAppName,
            publisherAAID = publisherAAID,
            response = object : ServeResponse<ResPublisherInitialize> {
                override fun onSuccess(success: ResPublisherInitialize) = response(Contract.onSuccess(success.publisher))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            }
        )
    }
}