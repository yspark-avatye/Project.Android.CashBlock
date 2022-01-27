package com.avatye.cashblock.base.internal.server.entity.offerwall

import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallContactRewardsEntity
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import com.avatye.cashblock.base.library.miscellaneous.toIntValue
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import org.json.JSONObject

internal class ResOfferwallContactRewards : ServeSuccess() {
    var offerwallContactRewardEntity = OfferwallContactRewardsEntity()
        private set

    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            offerwallContactRewardEntity = OfferwallContactRewardsEntity(
                adUniqueID = it.toStringValue("adUniqueID"),
                contactID = it.toStringValue("contactID"),
                advertiseID = it.toStringValue("advertiseID"),
                title = it.toStringValue("title"),
                contents = it.toStringValue("contents"),
                type = it.toIntValue("type"),
                state = it.toIntValue("state"),
                deviceID = it.toStringValue("deviceID"),
                deviceADID = it.toStringValue("deviceADID"),
                phone = it.toStringValue("phone"),
                userName = it.toStringValue("userName"),
                resultMsgType = it.toIntValue("resultMsgType"),
                customMsg = it.toStringValue("customMsg")
            )
        }
    }
}