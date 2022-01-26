package com.avatye.cashblock.base.component.domain.entity.offerwall

enum class OfferWallListType(val value:Int) {
    MIX(1),
    ONLY_SECTION(2),
    ONLY_CATEGORY(3);

    @Override
    fun equals(other: Int): Boolean = this.value == other

    internal companion object {
        fun from(value: Int): OfferWallListType = values().find { it.value == value } ?: MIX
    }
}