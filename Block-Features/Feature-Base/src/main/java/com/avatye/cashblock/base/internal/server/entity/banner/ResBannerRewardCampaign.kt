package com.avatye.cashblock.base.internal.server.entity.banner

import com.avatye.cashblock.base.component.domain.entity.banner.BannerRewardCampaignEntity
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import com.avatye.cashblock.base.library.miscellaneous.toIntValue
import com.avatye.cashblock.base.library.miscellaneous.toLongValue
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import org.json.JSONObject

internal class ResBannerRewardCampaign : ServeSuccess() {
    var bannerRewardCampaignEntity = BannerRewardCampaignEntity()
        private set

    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            bannerRewardCampaignEntity = BannerRewardCampaignEntity(
                advertiseID = it.toStringValue("advertiseID"),
                imageUrl = it.toStringValue("imageUrl"),
                reward = it.toIntValue("reward"),
                limitSeconds = it.toLongValue("limitSeconds")
            )
        }
    }
}