package com.avatye.cashblock.base.component.domain.entity.offerwall

import org.joda.time.DateTime

data class OfferwallImpressionItemEntity(
    val advertiseID: String = "",
    val productID: OfferwallProductType = OfferwallProductType.NONE,
    val title: String = "",
    val displayTitle: String = "",
    val iconUrl: String = "",
    val state: Int = 0,
    val userGuide: String = "",
    val actionName: String = "",
    val reward: RewardItem? = null,
    val packageName: String? = "",
    val journeyState: OfferwallJourneyStateType = OfferwallJourneyStateType.NONE,
    val impressionID: String = "",
    val actionGuide: String = "",
    val clickID: String? = "",
    val clickDateTime: DateTime? = null,
    val pointName: String = "",
    val contactState: Int = -1,
) {
    data class RewardItem(
        var appRevenue: Int = 0,
        var ImpressionReward: Int = 0
    )
}
