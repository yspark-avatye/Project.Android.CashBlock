package com.avatye.cashblock.base.component.contract.data

import com.avatye.cashblock.base.component.domain.entity.app.PublisherInitializeEntity
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.internal.server.APIPublisher
import com.avatye.cashblock.base.internal.server.entity.app.ResPublisherInitialize
import com.avatye.cashblock.base.internal.server.serve.IServeToken
import com.avatye.cashblock.base.internal.server.serve.ServeFailure
import com.avatye.cashblock.base.internal.server.serve.ServeResponse

class PublisherDataContract() {
    // publisher not use appid
    fun retrieveInitialize(
        publisherID: String,
        publisherAppKey: String,
        publisherAppName: String,
        publisherAAID: String,
        response: (contract: ContractResult<PublisherInitializeEntity>) -> Unit
    ) {
        APIPublisher.getInitialize(
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