package com.avatye.cashblock.base.component.contract.api

import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.component.domain.entity.offerwall.*
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.internal.server.APIOfferwall
import com.avatye.cashblock.base.internal.server.entity.ResVoid
import com.avatye.cashblock.base.internal.server.entity.offerwall.*
import com.avatye.cashblock.base.internal.server.serve.ServeFailure
import com.avatye.cashblock.base.internal.server.serve.ServeResponse

class OfferwallApiContractor(private val blockType: BlockType) {

    fun retrieveList(deviceADID: String, tabID: String? = null, serviceID: ServiceType, response: (contract: ContractResult<MutableList<OfferwallSectionEntity>>) -> Unit) {
        APIOfferwall.getOfferwalls(
            blockType = blockType,
            deviceADID = deviceADID,
            tabID = tabID,
            service = serviceID,
            response = object : ServeResponse<ResOfferwallList> {
                override fun onSuccess(success: ResOfferwallList) = response(Contract.onSuccess(success = success.sections))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure = failure))
            })
    }

    fun retrieveAvailableReward(deviceADID: String, service: ServiceType, response: (contract: ContractResult<OfferwallAvailableRewardEntity>) -> Unit) {
        APIOfferwall.getOfferwallsAvailableReward(
            blockType = blockType,
            deviceADID = deviceADID,
            service = service,
            response = object : ServeResponse<ResOfferWallAvailableReward> {
                override fun onSuccess(success: ResOfferWallAvailableReward) = response(Contract.onSuccess(success = success.offerwallAvailableRewardEntity))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure = failure))
            })
    }

    fun postImpression(deviceADID: String, advertiseID: String, service: ServiceType, response: (contract: ContractResult<OfferwallImpressionItemEntity>) -> Unit) {
        APIOfferwall.postOfferwallImpression(
            blockType = blockType,
            deviceADID = deviceADID,
            advertiseID = advertiseID,
            service = service,
            response = object : ServeResponse<ResOfferWallImpression> {
                override fun onSuccess(success: ResOfferWallImpression) = response(Contract.onSuccess(success = success.offerwallImpressionItemEntity))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure = failure))
            })
    }

    fun postClose(deviceADID: String, advertiseID: String, response: (contract: ContractResult<Unit>) -> Unit) {
        APIOfferwall.postOfferwallClose(
            blockType = blockType,
            deviceADID = deviceADID,
            advertiseID = advertiseID,
            response = object : ServeResponse<ResVoid> {
                override fun onSuccess(success: ResVoid) = response(Contract.onSuccess(success = Unit))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure = failure))
            })
    }

    fun postClick(
        deviceADID: String,
        advertiseID: String,
        deviceID: String? = null,
        deviceModel: String? = null,
        deviceNetwork: String? = null,
        deviceOS: String? = null,
        deviceCarrier: String? = null,
        customData: String? = null,
        service: ServiceType,
        response: (contract: ContractResult<OfferwallClickEntity>) -> Unit
    ) {
        APIOfferwall.postOfferwallClick(
            blockType = blockType,
            deviceADID = deviceADID,
            advertiseID = advertiseID,
            deviceID = deviceID,
            deviceModel = deviceModel,
            deviceNetwork = deviceNetwork,
            deviceOS = deviceOS,
            deviceCarrier = deviceCarrier,
            customData = customData,
            service = service,
            response = object : ServeResponse<ResOfferWallClick> {
                override fun onSuccess(success: ResOfferWallClick) = response(Contract.onSuccess(success = success.offerwallClickEntity))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure = failure))
            })
    }

    fun postConversion(
        deviceADID: String,
        advertiseID: String,
        clickID: String,
        deviceID: String? = null,
        deviceNetwork: String? = null,
        service: ServiceType,
        response: (contract: ContractResult<Unit>) -> Unit
    ) {
        APIOfferwall.postOfferwallConversion(
            blockType = blockType,
            deviceADID = deviceADID,
            advertiseID = advertiseID,
            clickID = clickID,
            deviceID = deviceID,
            deviceNetwork = deviceNetwork,
            service = service,
            response = object : ServeResponse<ResVoid> {
                override fun onSuccess(success: ResVoid) = response(Contract.onSuccess(success = Unit))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure = failure))
            })
    }

    fun postContactReward(
        contactID: String? = null,
        advertiseID: String,
        title: String? = null,
        contents: String,
        state: Int? = null,
        resultMsgType: Int? = null,
        deviceID: String? = null,
        deviceADID: String,
        phone: String? = null,
        userName: String? = null,
        response: (contract: ContractResult<Unit>) -> Unit
    ) {
        APIOfferwall.postOfferwallContactReward(
            blockType = blockType,
            contactID = contactID,
            advertiseID = advertiseID,
            title = title,
            contents = contents,
            state = state,
            resultMsgType = resultMsgType,
            deviceID = deviceID,
            deviceADID = deviceADID,
            phone = phone,
            userName = userName,
            response = object : ServeResponse<ResVoid> {
                override fun onSuccess(success: ResVoid) = response(Contract.onSuccess(success = Unit))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure = failure))
            })
    }

    fun retrieveContactRewardInfo(advertiseID: String, response: (contract: ContractResult<OfferwallContactRewardInfoEntity>) -> Unit) {
        APIOfferwall.getOfferwallContactRewardInfo(
            blockType = blockType,
            advertiseID = advertiseID,
            response = object : ServeResponse<ResOfferwallContactRewardInfo> {
                override fun onSuccess(success: ResOfferwallContactRewardInfo) = response(Contract.onSuccess(success = success.contactRewardInfo))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure = failure))
            })
    }

    fun retrieveContactRewardList(blockType: BlockType, response: (contract: ContractResult<MutableList<OfferwallContactRewardEntity>>) -> Unit) {
        APIOfferwall.getOfferwallContactRewards(
            blockType = blockType,
            response = object : ServeResponse<ResOfferwallContactRewards> {
                override fun onSuccess(success: ResOfferwallContactRewards) = response(Contract.onSuccess(success = success.contactRewards))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure = failure))
            })
    }

    fun retrieveTabs(response: (contract: ContractResult<MutableList<OfferWallTabEntity>>) -> Unit) {
        APIOfferwall.getOfferwallTabs(
            blockType = blockType,
            response = object : ServeResponse<ResOfferwallTabs> {
                override fun onSuccess(success: ResOfferwallTabs) = response(Contract.onSuccess(success = success.tabEntities))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure = failure))
            })
    }
}