package com.avatye.cashblock.base.internal.server.entity.offerwall

import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallAvailableRewardEntity
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import com.avatye.cashblock.base.library.miscellaneous.toIntValue
import org.json.JSONObject

internal class ResOfferWallAvailableReward : ServeSuccess() {
    var offerwallAvailableRewardEntity = OfferwallAvailableRewardEntity()


    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            offerwallAvailableRewardEntity = OfferwallAvailableRewardEntity(
                totalAvailableReward = it.toIntValue("totalAvailableReward")
            )
        }


    }
}