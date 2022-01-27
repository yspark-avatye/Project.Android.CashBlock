package com.avatye.cashblock.base.internal.server

import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.internal.server.entity.ResVoid
import com.avatye.cashblock.base.internal.server.entity.offerwall.*
import com.avatye.cashblock.base.internal.server.serve.ServeResponse
import com.avatye.cashblock.base.internal.server.serve.ServeTask

internal object APIOfferwall {
    fun getOfferwalls(blockType: BlockType, deviceADID: String, tabID: String? = null, service: ServiceType, response: ServeResponse<ResOfferwallList>) {
        val bodyArgs = hashMapOf<String, Any>(
            "serviceID" to service.value,
            "deviceADID" to deviceADID,
        ).apply {
            tabID?.let {
                "tabID" to it
            }
        }
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.GET,
            requestUrl = "/advertising/offerwalls",
            acceptVersion = "1.0.0",
            argsBody = bodyArgs,
            responseClass = ResOfferwallList::class.java,
            responseCallback = response
        )
    }

    fun getOfferwallsAvailableReward(blockType: BlockType, deviceADID: String, service: ServiceType, response: ServeResponse<ResOfferWallAvailableReward>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.GET,
            requestUrl = "/advertising/offerwalls/available",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf(
                "deviceADID" to deviceADID,
                "serviceID" to service.value
            ),
            responseClass = ResOfferWallAvailableReward::class.java,
            responseCallback = response
        )
    }

    fun postOfferwallImpression(blockType: BlockType, deviceADID: String, advertiseID: String, service: ServiceType, response: ServeResponse<ResOfferWallImpression>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.POST,
            requestUrl = "/advertising/offerwall/impression",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf(
                "deviceADID" to deviceADID,
                "advertiseID" to advertiseID,
                "serviceID" to service.value
            ),
            responseClass = ResOfferWallImpression::class.java,
            responseCallback = response
        )
    }

    fun postOfferwallClose(blockType: BlockType, deviceADID: String, advertiseID: String, response: ServeResponse<ResVoid>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.POST,
            requestUrl = "/advertising/offerwall/close",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf(
                "deviceADID" to deviceADID,
                "advertiseID" to advertiseID
            ),
            responseClass = ResVoid::class.java,
            responseCallback = response
        )
    }

    fun postOfferwallClick(
        blockType: BlockType,
        deviceADID: String,
        advertiseID: String,
        deviceID: String = "",
        deviceModel: String = "",
        deviceNetwork: String = "",
        deviceOS: String = "",
        deviceCarrier: String = "",
        customData: String = "",
        service: ServiceType,
        response: ServeResponse<ResOfferWallClick>
    ) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.POST,
            requestUrl = "/advertising/offerwall/click",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf(
                "deviceADID" to deviceADID, "advertiseID" to advertiseID, "deviceID" to deviceID, "deviceModel" to deviceModel,
                "deviceNetwork" to deviceNetwork, "deviceOS" to deviceOS, "deviceCarrier" to deviceCarrier, "customData" to customData, "serviceID" to service.value
            ),
            responseClass = ResOfferWallClick::class.java,
            responseCallback = response
        )
    }

    fun postOfferwallConversion(
        blockType: BlockType,
        deviceADID: String,
        advertiseID: String,
        clickID: String,
        deviceID: String = "",
        deviceNetwork: String = "",
        service: ServiceType,
        response: ServeResponse<ResVoid>
    ) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.POST,
            requestUrl = "/advertising/offerwall/conversion",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf(
                "deviceADID" to deviceADID,
                "advertiseID" to advertiseID,
                "clickID" to clickID,
                "deviceID" to deviceID,
                "deviceNetwork" to deviceNetwork,
                "serviceID" to service.value
            ),
            responseClass = ResVoid::class.java,
            responseCallback = response
        )
    }

    fun postOfferwallContactReward(
        blockType: BlockType,
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
        response: ServeResponse<ResVoid>
    ) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.POST,
            requestUrl = "/advertising/support/contact/reward",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf(
                "contactID" to contactID,
                "advertiseID" to advertiseID,
                "title" to title,
                "contents" to contents,
                "state" to state,
                "resultMsgType" to resultMsgType,
                "deviceID" to deviceID,
                "deviceADID" to deviceADID,
                "phone" to phone,
                "userName" to userName
            ),
            responseClass = ResVoid::class.java,
            responseCallback = response
        )
    }

    fun getOfferwallContactRewardInfo(blockType: BlockType, advertiseID: String, response: ServeResponse<ResVoid>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.GET,
            requestUrl = "/advertising/support/contact/reward/info",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf("advertiseID" to advertiseID),
            responseClass = ResVoid::class.java,
            responseCallback = response
        )
    }

    fun getOfferwallContactRewards(
        blockType: BlockType,
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
        response: ServeResponse<ResOfferwallContactRewards>
    ) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.GET,
            requestUrl = "/advertising/support/contact/rewards",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf(
                "contactID" to contactID,
                "advertiseID" to advertiseID,
                "title" to title,
                "contents" to contents,
                "type" to type,
                "state" to state,
                "deviceID" to deviceID,
                "deviceADID" to deviceADID,
                "phone" to phone,
                "userName" to userName,
                "resultMsgType" to resultMsgType,
                "customMsg" to customMsg,
            ),
            responseClass = ResOfferwallContactRewards::class.java,
            responseCallback = response
        )
    }


}