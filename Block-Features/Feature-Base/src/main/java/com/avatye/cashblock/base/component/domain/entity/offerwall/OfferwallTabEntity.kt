package com.avatye.cashblock.base.component.domain.entity.offerwall

data class OfferWallTabEntity(
    val tabID: String = "",
    val tabName: String = "",
    val listType: OfferWallListType = OfferWallListType.MIX,
    val sortOrder: Int = 0,
    val sectionList: List<String> = listOf()
)