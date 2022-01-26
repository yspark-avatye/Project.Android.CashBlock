package com.avatye.cashblock.base.component.domain.entity.offerwall

data class OfferwallImpressionItemEntity(
    var advertiseID: String = "",
    var productID: String = "",
    var title: String = "",
    var iconUrl: String = "",
    var state: Int = 0,
    var userGuide: String = "",
    var actionName: String = "",
    var reward: Int = 0,
    var packageName: String? = "",
    var journeyState: Int = 0,
    var impressionID: String = "",
    var actionGuide: String = "",
    var clickID: String? = "",
)
