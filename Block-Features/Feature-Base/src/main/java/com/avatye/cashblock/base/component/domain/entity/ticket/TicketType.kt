package com.avatye.cashblock.base.component.domain.entity.ticket

enum class TicketType(val value: Int) {

    TOUCH(1), VIDEO(3);

    companion object {
        fun from(value: Int): TicketType {
            return if (value == 1) TOUCH else VIDEO
        }
    }

}