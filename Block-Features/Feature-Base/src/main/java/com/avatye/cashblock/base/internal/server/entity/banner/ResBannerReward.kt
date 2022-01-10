package com.avatye.cashblock.base.internal.server.entity.banner

import com.avatye.cashblock.base.component.domain.entity.banner.BannerRewardEntity
import com.avatye.cashblock.base.library.miscellaneous.toBooleanValue
import com.avatye.cashblock.base.library.miscellaneous.toIntValue
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import org.json.JSONObject

class ResBannerReward : ServeSuccess() {
    var bannerRewardEntity = BannerRewardEntity()
        private set

    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            bannerRewardEntity = BannerRewardEntity(
                rewardable = it.toBooleanValue("rewardable", false),
                reward = it.toIntValue("reward", 0),
                transactionId = it.toStringValue("ticketTransactionID")
            )
        }
    }
}