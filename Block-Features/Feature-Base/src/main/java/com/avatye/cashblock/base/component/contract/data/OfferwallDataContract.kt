package com.avatye.cashblock.base.component.contract.data

import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.component.contract.AccountContract
import com.avatye.cashblock.base.internal.server.serve.IServeToken

class OfferwallDataContract(private val blockCode: BlockCode) {
    private val appId = blockCode.blockId

    private val tokenizer = object : IServeToken {
        override fun makeBasicToken() = blockCode.basicToken
        override fun makeBearerToken() = AccountContract.accessToken
    }


    //        fun retrieveList(response: (contract: ContractResult<OfferwallListModel>) -> Unit) {
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
//        fun postImpression(advertiseID: String, response: (contract: ContractResult<OfferWallImpressionModel>) -> Unit) {
//            APIOfferwall.postOfferwallImpression(tag = tag, advertiseID = advertiseID, response = object : ServeResponse<ResOfferWallImpression> {
//                override fun onSuccess(success: ResOfferWallImpression) = response(Contract.onSuccess(success.responseModel))
//                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
//            })
//        }
//
//        fun postClick(
//            tag: String,
//            advertiseID: String,
//            impressionID: String,
//            deviceADID: String,
//            androidID: String,
//            response: (contract: ContractResult<OfferwallClickModel>) -> Unit
//        ) {
//            APIOfferwall.postOfferwallClick(
//                tag = tag,
//                advertiseID = advertiseID,
//                impressionID = impressionID,
//                deviceADID = deviceADID,
//                androidID = androidID,
//                response = object : ServeResponse<ResOfferwallClick> {
//                    override fun onSuccess(success: ResOfferwallClick) = response(Contract.onSuccess(success.responseModel))
//                    override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
//                }
//            )
//        }
//
//        fun postConversion(
//            tag: String,
//            advertiseID: String,
//            clickID: String,
//            deviceADID: String,
//            androidID: String,
//            response: (contract: ContractResult<Unit>) -> Unit
//        ) {
//            APIOfferwall.postOfferwallConversion(
//                tag = tag,
//                advertiseID = advertiseID,
//                clickID = clickID,
//                deviceADID = deviceADID,
//                androidID = androidID,
//                response = object : ServeResponse<ResVoid> {
//                    override fun onSuccess(success: ResVoid) = response(Contract.onSuccess(Unit))
//                    override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
//                }
//            )
//        }
//
//        fun postClose(advertiseID: String, response: (contract: ContractResult<Unit>) -> Unit) {
//            APIOfferwall.postOfferwallClose(tag = tag, advertiseID = advertiseID, response = object : ServeResponse<ResVoid> {
//                override fun onSuccess(success: ResVoid) = response(Contract.onSuccess(Unit))
//                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
//            })
//        }
}