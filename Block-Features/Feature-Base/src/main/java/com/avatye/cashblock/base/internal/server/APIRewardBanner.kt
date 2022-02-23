package com.avatye.cashblock.base.internal.server

import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.internal.server.entity.ResVoid
import com.avatye.cashblock.base.internal.server.entity.banner.ResBannerBalance
import com.avatye.cashblock.base.internal.server.entity.banner.ResBannerReward
import com.avatye.cashblock.base.internal.server.entity.banner.ResBannerRewardCampaign
import com.avatye.cashblock.base.internal.server.entity.banner.ResBannerRewardParticipate
import com.avatye.cashblock.base.internal.server.serve.ServeResponse
import com.avatye.cashblock.base.internal.server.serve.ServeTask

internal object APIRewardBanner {
    fun getBannerDirectReward(blockType: BlockType, response: ServeResponse<ResBannerReward>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.GET,
            requestUrl = "advertising/banner/direct/reward",
            acceptVersion = "1.0.0",
            responseClass = ResBannerReward::class.java,
            responseCallback = response
        ).execute()
    }

    fun postBannerDirectReward(blockType: BlockType, ticketTransactionId: String, response: ServeResponse<ResBannerBalance>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.POST,
            requestUrl = "advertising/banner/direct/reward",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf("ticketTransactionID" to ticketTransactionId),
            responseClass = ResBannerBalance::class.java,
            responseCallback = response
        ).execute()
    }

    // region # QB
    fun getBannerRewardCampaign(blockType: BlockType, deviceAAID: String, serviceID: String, response: ServeResponse<ResBannerRewardCampaign>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.GET,
            requestUrl = "advertising/rewardCPC",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf(
                "deviceADID" to deviceAAID,
                "serviceID" to serviceID
            ),
            responseClass = ResBannerRewardCampaign::class.java,
            responseCallback = response
        ).execute()
    }

    fun postBannerRewardParticipate(blockType: BlockType, deviceAAID: String, advertiseID: String, serviceID: String, response: ServeResponse<ResBannerRewardParticipate>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.POST,
            requestUrl = "advertising/rewardCPC",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf(
                "deviceADID" to deviceAAID,
                "advertiseID" to advertiseID,
                "serviceID" to serviceID
            ),
            responseClass = ResBannerRewardParticipate::class.java,
            responseCallback = response
        ).execute()
    }

    fun putBannerRewardComplete(blockType: BlockType, clickID: String, serviceID: String, response: ServeResponse<ResVoid>) {
        ServeTask(
            blockType = blockType,
            authorization = ServeTask.Authorization.BEARER,
            method = ServeTask.Method.PUT,
            requestUrl = "advertising/rewardCPC",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf(
                "clickID" to clickID,
                "serviceID" to serviceID
            ),
            responseClass = ResVoid::class.java,
            responseCallback = response
        ).execute()
    }
    // endregion

}