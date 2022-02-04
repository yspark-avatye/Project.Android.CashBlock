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
            requestUrl = "advertising/offerwalls",
            acceptVersion = "1.0.0",
            argsBody = bodyArgs,
            responseClass = ResOfferwallList::class.java,
            responseCallback = response
        ).execute()
    }

    fun getOfferwallsAvailableReward(blockType: BlockType, deviceADID: String, service: ServiceType, response: ServeResponse<ResOfferWallAvailableReward>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.GET,
            requestUrl = "advertising/offerwalls/available",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf(
                "deviceADID" to deviceADID,
                "serviceID" to service.value
            ),
            responseClass = ResOfferWallAvailableReward::class.java,
            responseCallback = response
        ).execute()
    }

    fun postOfferwallImpression(blockType: BlockType, deviceADID: String, advertiseID: String, service: ServiceType, response: ServeResponse<ResOfferWallImpression>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.POST,
            requestUrl = "advertising/offerwall/impression",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf(
                "deviceADID" to deviceADID,
                "advertiseID" to advertiseID,
                "serviceID" to service.value
            ),
            responseClass = ResOfferWallImpression::class.java,
            responseCallback = response
        ).execute()
    }

    fun postOfferwallClose(blockType: BlockType, deviceADID: String, advertiseID: String, response: ServeResponse<ResVoid>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.POST,
            requestUrl = "advertising/offerwall/close",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf(
                "deviceADID" to deviceADID,
                "advertiseID" to advertiseID
            ),
            responseClass = ResVoid::class.java,
            responseCallback = response
        ).execute()
    }

    fun postOfferwallClick(
        blockType: BlockType,
        deviceADID: String,
        advertiseID: String,
        deviceID: String? = null,
        deviceModel: String? = null,
        deviceNetwork: String? = null,
        deviceOS: String? = null,
        deviceCarrier: String? = null,
        customData: String? = null,
        service: ServiceType,
        response: ServeResponse<ResOfferWallClick>
    ) {

        val bodyArgs = hashMapOf<String, Any>(
            "deviceADID" to deviceADID,
            "advertiseID" to advertiseID,
            "serviceID" to service.value,
        ).apply {
            deviceID?.let {
                "deviceID" to it
            }
            deviceModel?.let {
                "deviceModel" to it
            }
            deviceNetwork?.let {
                "deviceNetwork" to it
            }
            deviceOS?.let {
                "deviceOS" to it
            }
            deviceCarrier?.let {
                "deviceCarrier" to it
            }
            customData?.let {
                "customData" to it
            }
        }

        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.POST,
            requestUrl = "advertising/offerwall/click",
            acceptVersion = "1.0.0",
            argsBody = bodyArgs,
            responseClass = ResOfferWallClick::class.java,
            responseCallback = response
        ).execute()
    }

    fun postOfferwallConversion(
        blockType: BlockType,
        deviceADID: String,
        advertiseID: String,
        clickID: String,
        deviceID: String? = null,
        deviceNetwork: String? = null,
        service: ServiceType,
        response: ServeResponse<ResVoid>
    ) {
        val bodyArgs = hashMapOf<String, Any>(
            "deviceADID" to deviceADID,
            "advertiseID" to advertiseID,
            "clickID" to clickID,
            "serviceID" to service.value,
        ).apply {
            deviceID?.let {
                "deviceID" to it
            }
            deviceNetwork?.let {
                "deviceNetwork" to it
            }
        }
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.POST,
            requestUrl = "advertising/offerwall/conversion",
            acceptVersion = "1.0.0",
            argsBody = bodyArgs,
            responseClass = ResVoid::class.java,
            responseCallback = response
        ).execute()
    }

    fun postOfferwallContactReward(
        blockType: BlockType,
        contactID: String? = null,
        advertiseID: String,
        title: String? = null,
        contents: String,
        state: Int = 0,
        resultMsgType: Int = 0,
        deviceID: String? = null,
        deviceADID: String,
        phone: String? = null,
        userName: String? = null,
        response: ServeResponse<ResVoid>
    ) {
        val bodyArgs = hashMapOf<String, Any>(
            "advertiseID" to advertiseID,
            "contents" to contents,
            "state" to state,
            "resultMsgType" to resultMsgType,
            "deviceADID" to deviceADID,
        ).apply {
            contactID?.let {
                "contactID" to it
            }
            title?.let {
                "title" to it
            }
            deviceID?.let {
                "deviceID" to deviceID
            }
            phone?.let {
                "phone" to phone
            }
            userName?.let {
                "userName" to userName
            }
        }
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.POST,
            requestUrl = "advertising/support/contact/reward",
            acceptVersion = "1.0.0",
            argsBody = bodyArgs,
            responseClass = ResVoid::class.java,
            responseCallback = response
        ).execute()
    }

    fun getOfferwallContactRewardInfo(blockType: BlockType, advertiseID: String, response: ServeResponse<ResVoid>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.GET,
            requestUrl = "advertising/support/contact/reward/info",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf("advertiseID" to advertiseID),
            responseClass = ResVoid::class.java,
            responseCallback = response
        ).execute()
    }

    fun getOfferwallContactRewards(
        blockType: BlockType,
        contactID: String? = null,
        advertiseID: String,
        title: String? = null,
        contents: String,
        type: Int = 0,
        state: Int = 0,
        deviceID: String? = null,
        deviceADID: String,
        phone: String? = null,
        userName: String? = null,
        resultMsgType: Int = 0,
        customMsg: String? = null,
        response: ServeResponse<ResOfferwallContactRewards>
    ) {
        val bodyArgs = hashMapOf<String, Any>(
            "advertiseID" to advertiseID,
            "contents" to contents,
            "type" to type,
            "state" to state,
            "deviceADID" to deviceADID,
            "resultMsgType" to resultMsgType,
        ).apply {
            contactID?.let {
                "contactID" to it
            }
            title?.let {
                "title" to it
            }
            deviceID?.let {
                "deviceID" to it
            }
            phone?.let {
                "phone" to it
            }
            userName?.let {
                "userName" to it
            }
            customMsg?.let {
                "customMsg" to it
            }
        }
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.GET,
            requestUrl = "advertising/support/contact/rewards",
            acceptVersion = "1.0.0",
            argsBody = bodyArgs,
            responseClass = ResOfferwallContactRewards::class.java,
            responseCallback = response
        ).execute()
    }


    fun getOfferwallTabs(blockType: BlockType, response: ServeResponse<ResOfferwallTabs>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.GET,
            requestUrl = "advertising/offerwall/tabs",
            acceptVersion = "1.0.0",
            argsBody = null,
            responseClass = ResOfferwallTabs::class.java,
            responseCallback = response,
        ).execute()
    }


}