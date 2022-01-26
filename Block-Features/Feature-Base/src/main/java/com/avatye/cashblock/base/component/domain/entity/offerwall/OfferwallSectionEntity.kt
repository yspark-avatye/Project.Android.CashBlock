package com.avatye.cashblock.base.component.domain.entity.offerwall

data class OfferwallSectionEntity(
    var sectionID: String = "",
    var sectionName: String = "",
    var sectionTitle: String = "",
    var sectionSortOrder: Int = 0,
    var items: MutableList<OfferwallItemEntity> = mutableListOf(),
    var categories: MutableList<OfferwallCategoryEntity> = mutableListOf(),
) {
    data class OfferwallCategoryEntity(
        var categoryID: String = "",
        var categoryName: String = "",
        var categorySortOrder: Int = 0,
        var items: MutableList<OfferwallItemEntity> = mutableListOf()
    )
}