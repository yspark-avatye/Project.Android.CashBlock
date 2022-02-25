package com.avatye.cashblock.base.internal.server.entity.offerwall

import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallProductType
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallImpressionItemEntity
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallJourneyStateType
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import com.avatye.cashblock.base.library.miscellaneous.toDateTimeValue
import com.avatye.cashblock.base.library.miscellaneous.toIntValue
import com.avatye.cashblock.base.library.miscellaneous.toJSONObjectValue
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import org.json.JSONObject

internal class ResOfferWallImpression : ServeSuccess() {
    var offerwallImpressionItemEntity = OfferwallImpressionItemEntity()
        private set


    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {

            offerwallImpressionItemEntity = OfferwallImpressionItemEntity(
                advertiseID = it.toStringValue("advertiseID"),
                productID = OfferwallProductType.from(it.toStringValue("productID")),
                title = it.toStringValue("title"),
                displayTitle = it.toStringValue("displayTitle"),
                iconUrl = it.toStringValue("iconUrl"),
                state = it.toIntValue("state"),
                userGuide = it.toStringValue("userGuide"),
                actionName = it.toStringValue("actionName"),
                reward = it.toJSONObjectValue("reward")?.let { obj ->
                    OfferwallImpressionItemEntity.RewardItem(
                        appRevenue = obj.toIntValue("appRevenue"),
                        ImpressionReward = obj.toIntValue("reward"),
                    )
                },
                packageName = it.toStringValue("packageName"),
                journeyState = OfferwallJourneyStateType.from(it.toIntValue("journeyState")),
                impressionID = it.toStringValue("impressionID"),
                actionGuide = it.toStringValue("actionGuide"),
                clickID = it.toStringValue("clickID"),
                clickDateTime = it.toDateTimeValue("clickDateTime"),
                pointName = it.toStringValue("pointName"),
                contactState = it.toIntValue("contactState", -1),
            )
        }
    }
}