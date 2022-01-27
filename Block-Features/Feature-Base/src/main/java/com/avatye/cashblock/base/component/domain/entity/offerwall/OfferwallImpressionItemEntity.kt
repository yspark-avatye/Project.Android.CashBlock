package com.avatye.cashblock.base.component.domain.entity.offerwall

data class OfferwallImpressionItemEntity(
    val advertiseID: String = "",
    val productID: String = "",
    val title: String = "",
    val iconUrl: String = "",
    val state: Int = 0,
    val userGuide: String = "",
    val actionName: String = "",
    val reward: Int = 0,
    val packageName: String? = "",
    val journeyState: Int = 0,
    val impressionID: String = "",
    val actionGuide: String = "",
    val clickID: String? = "",
)
