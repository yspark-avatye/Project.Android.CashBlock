package com.avatye.cashblock.base.internal.server.entity.offerwall

import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallContactRewardInfoEntity
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import org.json.JSONObject

internal class ResOfferwallContactRewardInfo : ServeSuccess() {

    var contactRewardInfo = OfferwallContactRewardInfoEntity()
        private set

    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            contactRewardInfo = OfferwallContactRewardInfoEntity(
                contactRequireMsg = it.toStringValue("contactRequireMsg")
            )
        }
    }
}