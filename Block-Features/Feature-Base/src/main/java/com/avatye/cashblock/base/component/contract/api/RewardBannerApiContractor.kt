package com.avatye.cashblock.base.component.contract.api

import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.banner.BannerBalanceEntity
import com.avatye.cashblock.base.component.domain.entity.banner.BannerRewardEntity
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.internal.server.APIRewardBanner
import com.avatye.cashblock.base.internal.server.entity.banner.ResBannerBalance
import com.avatye.cashblock.base.internal.server.entity.banner.ResBannerReward
import com.avatye.cashblock.base.internal.server.serve.ServeFailure
import com.avatye.cashblock.base.internal.server.serve.ServeResponse

class RewardBannerApiContractor(private val blockType: BlockType) {
    fun retrieveDirectReward(response: (contract: ContractResult<BannerRewardEntity>) -> Unit) {
        APIRewardBanner.getBannerDirectReward(
            blockType = blockType,
            response = object : ServeResponse<ResBannerReward> {
                override fun onSuccess(success: ResBannerReward) = response(Contract.onSuccess(success.bannerRewardEntity))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }

    fun postDirectReward(transactionId: String, response: (contract: ContractResult<BannerBalanceEntity>) -> Unit) {
        APIRewardBanner.postBannerDirectReward(
            blockType = blockType,
            ticketTransactionId = transactionId,
            response = object : ServeResponse<ResBannerBalance> {
                override fun onSuccess(success: ResBannerBalance) = response(Contract.onSuccess(success.bannerBalanceEntity))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }
}