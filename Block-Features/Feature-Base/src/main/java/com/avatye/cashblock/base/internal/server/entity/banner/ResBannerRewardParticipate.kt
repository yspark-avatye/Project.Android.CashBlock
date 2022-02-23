package com.avatye.cashblock.base.internal.server.entity.banner

import com.avatye.cashblock.base.component.domain.entity.banner.BannerRewardParticipateEntity
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import org.json.JSONObject

internal class ResBannerRewardParticipate : ServeSuccess() {

    var bannerRewardParticipateEntity = BannerRewardParticipateEntity()
        private set

    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            bannerRewardParticipateEntity = BannerRewardParticipateEntity(
                clickID = it.toStringValue("clickID"),
                landingUrl = it.toStringValue("landingUrl")
            )
        }
    }
}