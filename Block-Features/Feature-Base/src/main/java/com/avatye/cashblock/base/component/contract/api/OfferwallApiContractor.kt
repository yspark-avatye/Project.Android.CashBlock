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

    fun retrieveList(deviceADID: String, tabID: String = "", serviceID: ServiceType, response: (contract: ContractResult<OfferwallItemEntity>) -> Unit) {
        APIOfferwall.getOfferwalls(
            blockType = blockType,
            deviceADID = deviceADID,
            tabID = tabID,
            service = serviceID,
            response = object : ServeResponse<ResOfferwallList> {
                override fun onSuccess(success: ResOfferwallList) = response(Contract.onSuccess(success = success.itemEntity))
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
            }
        )
    }

    fun postClose(deviceADID: String, advertiseID: String, response: (contract: ContractResult<Unit>) -> Unit) {
        APIOfferwall.postOfferwallClose(
            blockType = blockType,
            deviceADID = deviceADID,
            advertiseID = advertiseID,
            response = object : ServeResponse<ResVoid> {
                override fun onSuccess(success: ResVoid) = response(Contract.onSuccess(success = Unit))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure = failure))
            }
        )
    }

    fun postClick(
        deviceADID: String,
        advertiseID: String,
        deviceID: String = "",
        deviceModel: String = "",
        deviceNetwork: String = "",
        deviceOS: String = "",
        deviceCarrier: String = "",
        customData: String = "",
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
            }
        )
    }

    fun postConversion(
        deviceADID: String,
        advertiseID: String,
        clickID: String,
        deviceID: String = "",
        deviceNetwork: String = "",
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
            }
        )
    }

    fun postContactReward(
        contactID: String = "",
        advertiseID: String,
        title: String = "",
        contents: String,
        state: Int = 0,
        resultMsgType: Int = 0,
        deviceID: String = "",
        deviceADID: String,
        phone: String = "",
        userName: String = "",
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
            }
        )
    }

    fun retrieveContactRewardInfo(advertiseID: String, response: (contract: ContractResult<Unit>) -> Unit) {
        APIOfferwall.getOfferwallContactRewardInfo(
            blockType = blockType,
            advertiseID = advertiseID,
            response = object : ServeResponse<ResVoid> {
                override fun onSuccess(success: ResVoid) = response(Contract.onSuccess(success = Unit))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure = failure))
            }
        )
    }

    fun retrieveContactRewards(
        contactID: String = "",
        advertiseID: String,
        title: String = "",
        contents: String,
        type: Int = 0,
        state: Int = 0,
        deviceID: String = "",
        deviceADID: String,
        phone: String = "",
        userName: String = "",
        resultMsgType: Int = 0,
        customMsg: String = "",
        response: (contract: ContractResult<OfferwallContactRewardsEntity>) -> Unit
    ) {
        APIOfferwall.getOfferwallContactRewards(
            blockType = blockType,
            contactID = contactID,
            advertiseID = advertiseID,
            title = title,
            contents = contents,
            type = type,
            state = state,
            deviceID = deviceID,
            deviceADID = deviceADID,
            phone = phone,
            userName = userName,
            resultMsgType = resultMsgType,
            customMsg = customMsg,
            response = object : ServeResponse<ResOfferwallContactRewards> {
                override fun onSuccess(success: ResOfferwallContactRewards) = response(Contract.onSuccess(success = success.offerwallContactRewardEntity))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure = failure))
            }
        )
    }


//            APIOfferwall.getOfferwalls(tag = tag, response = object : ServeResponse<ResOfferwallList> {
//                override fun onSuccess(success: ResOfferwallList) = response(Contract.onSuccess(success.responseModel))
//                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
//            })
//        }
//
//        fun retrieveTabs(response: (contract: ContractResult<String>) -> Unit) {
//            APIOfferwall.getOfferwallTabs(tag = tag, response = object : ServeResponse<ResVoid> {
//                override fun onSuccess(success: ResVoid) = response(Contract.onSuccess(success.rawString))
//                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
//            })
//        }
//

}