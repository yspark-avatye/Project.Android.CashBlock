package com.avatye.cashblock.base.internal.server.entity.offerwall

import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallImpressionItemEntity
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import com.avatye.cashblock.base.library.miscellaneous.toIntValue
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import org.json.JSONObject

internal class ResOfferWallImpression : ServeSuccess() {
    var offerwallImpressionItemEntity = OfferwallImpressionItemEntity()

    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            offerwallImpressionItemEntity = OfferwallImpressionItemEntity(
                advertiseID = it.toStringValue("advertiseID"),
                productID = it.toStringValue("productID"),
                title = it.toStringValue("title"),
                iconUrl = it.toStringValue("iconUrl"),
                state = it.toIntValue("state"),
                userGuide = it.toStringValue("userGuide"),
                actionName = it.toStringValue("actionName"),
                reward = it.toIntValue("reward"),
                packageName = it.toStringValue("packageName"),
                journeyState = it.toIntValue("journeyState"),
                impressionID = it.toStringValue("impressionID"),
                actionGuide = it.toStringValue("actionGuide"),
                clickID = it.toStringValue("clickID")
            )
        }
    }
}