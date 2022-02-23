package com.avatye.cashblock.base.component.domain.entity.offerwall

data class OfferwallSectionEntity(
    val sectionID: String = "",
    val sectionName: String = "",
    val sectionTitle: String = "",
    val sectionSortOrder: Int = 0,
    val items: MutableList<OfferwallItemEntity> = mutableListOf(),
    val categories: MutableList<OfferwallCategoryEntity> = mutableListOf(),
) {
    data class OfferwallCategoryEntity(
        val categoryID: String = "",
        val categoryName: String = "",
        val categorySortOrder: Int = 0,
        val items: MutableList<OfferwallItemEntity> = mutableListOf()
    )
}