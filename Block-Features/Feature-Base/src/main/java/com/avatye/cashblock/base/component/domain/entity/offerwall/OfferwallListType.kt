package com.avatye.cashblock.base.component.domain.entity.offerwall

/***
 * ListType
 * 1: 전체 (MIX)
 * 2: 빠른적립, 쇼핑적립, 게임적립 (Only-Section)
 * 3: 목돈적립 (Only-Category)
 */

enum class OfferwallListType(val value:Int) {
    MIX(1),
    ONLY_SECTION(2),
    ONLY_CATEGORY(3);

    @Override
    fun equals(other: Int): Boolean = this.value == other

    internal companion object {
        fun from(value: Int): OfferwallListType = values().find { it.value == value } ?: MIX
    }
}