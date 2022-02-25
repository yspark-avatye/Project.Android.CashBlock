package com.avatye.cashblock.base.internal.server.entity.offerwall

import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallContactRewardEntity
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import com.avatye.cashblock.base.library.miscellaneous.toDateTimeValue
import com.avatye.cashblock.base.library.miscellaneous.toIntValue
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import com.avatye.cashblock.base.library.miscellaneous.until
import org.json.JSONArray

internal class ResOfferwallContactRewards : ServeSuccess() {

    val contactRewards = mutableListOf<OfferwallContactRewardEntity>()

    override fun makeBody(responseValue: String) {
        JSONArray(responseValue).until {
            contactRewards.add(
                OfferwallContactRewardEntity(
                    contactID = it.toStringValue("contactID"),
                    advertiseID = it.toStringValue("advertiseID"),
                    title = it.toStringValue("title"),
                    state = it.toIntValue("state"),
                    resultMsgType = it.toIntValue("resultMsgType"),
                    customMsg = it.toStringValue("customMsg"),
                    createDateTime = it.toDateTimeValue("createDateTime")
                )
            )
        }
    }
}