package com.avatye.cashblock.base.component.contract.api

import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.banner.BannerBalanceEntity
import com.avatye.cashblock.base.component.domain.entity.banner.BannerRewardCampaignEntity
import com.avatye.cashblock.base.component.domain.entity.banner.BannerRewardEntity
import com.avatye.cashblock.base.component.domain.entity.banner.BannerRewardParticipateEntity
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.internal.server.APIRewardBanner
import com.avatye.cashblock.base.internal.server.entity.ResVoid
import com.avatye.cashblock.base.internal.server.entity.banner.ResBannerBalance
import com.avatye.cashblock.base.internal.server.entity.banner.ResBannerReward
import com.avatye.cashblock.base.internal.server.entity.banner.ResBannerRewardCampaign
import com.avatye.cashblock.base.internal.server.entity.banner.ResBannerRewardParticipate
import com.avatye.cashblock.base.internal.server.serve.ServeFailure
import com.avatye.cashblock.base.internal.server.serve.ServeResponse

class RewardBannerApiContractor(private val blockType: BlockType, private val serviceType: ServiceType) {
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

    // region # QB
    fun retrieveRewardCampaign(deviceAAID: String, response: (contract: ContractResult<BannerRewardCampaignEntity>) -> Unit) {
        APIRewardBanner.getBannerRewardCampaign(
            blockType = blockType,
            deviceAAID = deviceAAID,
            serviceID = serviceType.value,
            response = object : ServeResponse<ResBannerRewardCampaign> {
                override fun onSuccess(success: ResBannerRewardCampaign) = response(Contract.onSuccess(success = success.bannerRewardCampaignEntity))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }

    fun postRewardParticipate(deviceAAID: String, advertiseID: String, response: (ContractResult<BannerRewardParticipateEntity>) -> Unit) {
        APIRewardBanner.postBannerRewardParticipate(
            blockType = blockType,
            deviceAAID = deviceAAID,
            advertiseID = advertiseID,
            serviceID = serviceType.value,
            response = object : ServeResponse<ResBannerRewardParticipate> {
                override fun onSuccess(success: ResBannerRewardParticipate) = response(Contract.onSuccess(success = success.bannerRewardParticipateEntity))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }

    fun putRewardComplete(clickID: String, response: (ContractResult<Unit>) -> Unit) {
        APIRewardBanner.putBannerRewardComplete(
            blockType = blockType,
            clickID = clickID,
            serviceID = serviceType.value,
            response = object : ServeResponse<ResVoid> {
                override fun onSuccess(success: ResVoid) = response(Contract.onSuccess(success = Unit))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }
    // endregion
}