package com.avatye.cashblock.base.internal.server

import com.avatye.cashblock.base.internal.server.entity.banner.ResBannerBalance
import com.avatye.cashblock.base.internal.server.entity.banner.ResBannerReward
import com.avatye.cashblock.base.internal.server.serve.IServeToken
import com.avatye.cashblock.base.internal.server.serve.ServeTask
import com.avatye.cashblock.base.internal.server.serve.ServeResponse

object APIRewardBanner {
    fun getBannerDirectReward(appId: String, tokenizer: IServeToken, response: ServeResponse<ResBannerReward>) {
        ServeTask(
            appId = appId,
            authorization = ServeTask.Authorization.BEARER,
            tokenizer = tokenizer,
            method = ServeTask.Method.GET,
            requestUrl = "advertising/banner/direct/reward",
            acceptVersion = "1.0.0",
            responseClass = ResBannerReward::class.java,
            responseCallback = response
        ).execute()
    }

    fun postBannerDirectReward(appId: String, tokenizer: IServeToken, ticketTransactionId: String, response: ServeResponse<ResBannerBalance>) {
        ServeTask(
            appId = appId,
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