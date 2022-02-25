package com.avatye.cashblock.base.component.domain.entity.offerwall

data class OfferwallTabEntity(
    val tabID: String = "",
    val tabName: String = "",
    val listType: Int = OfferwallListType.MIX.value,
    val sortOrder: Int = 0,
    val sectionIDList: List<String> = listOf(),
)
