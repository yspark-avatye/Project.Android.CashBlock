package com.avatye.cashblock.base.internal.server.entity.offerwall

import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallContactRewardEntity
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import org.json.JSONObject

internal class ResContactRewardInfo : ServeSuccess() {
    var offerwallContactRewardInfoEntity = OfferwallContactRewardEntity()
        private set

    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            offerwallContactRewardInfoEntity = OfferwallContactRewardEntity(
                contactRequireMsg = it.toStringValue("contactRequireMsg")
            )
        }
    }
}