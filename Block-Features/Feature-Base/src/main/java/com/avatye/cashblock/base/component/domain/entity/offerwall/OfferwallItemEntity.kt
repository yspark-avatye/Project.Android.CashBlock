package com.avatye.cashblock.base.component.domain.entity.offerwall

data class OfferwallItemEntity(
    val advertiseID: String = "",
    val productID: String = "",
    val badgeIconUrl: String = "",
    val title: String = "",
    val displayTitle: String = "",
    val iconUrl: String = "",
    val state: Int = 0,
    val userGuide: String = "",
    val actionName: String = "",
    val reward: Int = 0,
    val packageName: String = "",
    val journeyState: OfferwallJourneyStateType = OfferwallJourneyStateType.NONE,
    val actionBGColor: String = "",
    val additionalDescription: String = "",
) {
    // 섹션 개수
    var sectionCount: Int = 0

    // 카테고리 개수
    var categoryCount: Int = 0

    // Last item 여부
    var isLast: Boolean = false

    // View State Type

    var viewType: OfferwallViewStateType = OfferwallViewStateType.VIEW_TYPE_ITEM
}

