package com.avatye.cashblock.base.component.domain.entity.offerwall

data class OfferwallImpressionItemEntity(
    val advertiseID: String = "",
    val productID: OfferWallProductType = OfferWallProductType.NONE,
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
    val pointName: String? = "",
) {
    data class RewardItem(
        var appRevenue: Int = 0,
        var ImpressionReward: Int = 0
    )
}
