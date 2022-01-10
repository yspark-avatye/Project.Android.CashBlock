package com.avatye.cashblock.base.component.domain.entity.box

enum class BoxType(val value: Int) {
    NONE(0),
    TICKET_BOX(1);

    internal companion object {
        fun from(value: Int): BoxType {
            return if (value == 1) TICKET_BOX else NONE
        }
    }
}