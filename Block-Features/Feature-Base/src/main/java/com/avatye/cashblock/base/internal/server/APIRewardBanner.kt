package com.avatye.cashblock.base.internal.server

import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.internal.server.entity.banner.ResBannerBalance
import com.avatye.cashblock.base.internal.server.entity.banner.ResBannerReward
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

}