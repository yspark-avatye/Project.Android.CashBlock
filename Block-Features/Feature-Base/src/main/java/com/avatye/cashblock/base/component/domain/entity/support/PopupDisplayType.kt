package com.avatye.cashblock.base.component.domain.entity.support

enum class PopupDisplayType(val value: Int) {
    ONETIME(1),
    TODAY(2),
    NEVER(3),
    WEEK(4);

    @Override
    fun equals(other: Int): Boolean = this.value == other

    companion object {
        fun from(value: Int): PopupDisplayType = values().find { it.value == value } ?: ONETIME
    }
}