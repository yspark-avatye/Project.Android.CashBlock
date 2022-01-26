package com.avatye.cashblock.base.component.domain.entity.offerwall

data class OfferwallItemEntity(
    var advertiseID: String = "",
    var productID: String = "",
    var badgeIconUrl: String = "",
    var title: String = "",
    var displayTitle: String = "",
    var iconUrl: String = "",
    var state: Int = 0,
    var userGuide: String = "",
    var actionName: String = "",
    var reward: Int = 0,
    var packageName: String = "",
    var journeyState: OfferwallJourneyStateType = OfferwallJourneyStateType.NONE,
    var actionBGColor: String = "",
    var additionalDescription: String = "",
) {

    // 섹션 개수
    var sectionCount: Int = 0
        set(value) {
            field = value
        }
        get() {
            return field
        }

    // 카테고리 개수
    var categoryCount: Int = 0
        set(value) {
            field = value
        }
        get() {
            return field
        }

    // Last item 여부
    var isLast: Boolean = false
        set(value) {
            field = value
        }
        get() {
            return field
        }

    // View State Type

    var viewType : OfferwallViewStateType = OfferwallViewStateType.VIEW_TYPE_ITEM
        set(value) {
            field = value
        }
        get() {
            return field
        }


}

