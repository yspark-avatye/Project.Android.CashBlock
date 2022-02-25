package com.avatye.cashblock.base.component.domain.entity.offerwall

/**
광고 리스트 뷰 상태
 * 0: item
 * 1: category
 * 2: category item
 * 3: section
 * */
enum class OfferwallViewStateType (val value: Int) {
    VIEW_TYPE_ITEM(0),
    VIEW_TYPE_CATEGORY(1),
    VIEW_TYPE_CATEGORY_ITEM(2),
    VIEW_TYPE_SECTION(3);

    companion object {
        fun from(value: Int): OfferwallViewStateType {
            return when (value) {
                0 -> VIEW_TYPE_ITEM
                1 -> VIEW_TYPE_CATEGORY
                2 -> VIEW_TYPE_CATEGORY_ITEM
                3 -> VIEW_TYPE_SECTION
                else -> VIEW_TYPE_ITEM
            }
        }
    }
}