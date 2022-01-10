package com.avatye.cashblock.base.internal.server

import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.internal.server.entity.banner.ResBannerBalance
import com.avatye.cashblock.base.internal.server.entity.banner.ResBannerReward
import com.avatye.cashblock.base.internal.server.serve.IServeToken
import com.avatye.cashblock.base.internal.server.serve.ServeResponse
import com.avatye.cashblock.base.internal.server.serve.ServeTask

object APIRewardBanner {
    fun getBannerDirectReward(blockCode: BlockCode, tokenizer: IServeToken, response: ServeResponse<ResBannerReward>) {
        ServeTask(
            blockCode = blockCode,
            authorization = ServeTask.Authorization.BEARER,
            tokenizer = tokenizer,
            method = ServeTask.Method.GET,
            requestUrl = "advertising/banner/direct/reward",
            acceptVersion = "1.0.0",
            responseClass = ResBannerReward::class.java,
            responseCallback = response
        ).execute()
    }

    fun postBannerDirectReward(blockCode: BlockCode, tokenizer: IServeToken, ticketTransactionId: String, response: ServeResponse<ResBannerBalance>) {
        ServeTask(
            blockCode = blockCode,
            authorization = ServeTask.Authorization.BEARER,
            tokenizer = tokenizer,
            method = ServeTask.Method.POST,
            requestUrl = "advertising/banner/direct/reward",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf("ticketTransactionID" to ticketTransactionId),
            responseClass = ResBannerBalance::class.java,
            responseCallback = response
        ).execute()
    }

}