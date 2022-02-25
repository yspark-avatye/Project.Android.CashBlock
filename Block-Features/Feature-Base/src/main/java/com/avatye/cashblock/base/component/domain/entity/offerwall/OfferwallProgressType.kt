package com.avatye.cashblock.base.component.domain.entity.offerwall

enum class OfferwallProgressType(val value: Int) {
    INPROGRESS(0),
    COMPLETED(1);

    internal companion object {
        fun from(value:Int) : OfferwallProgressType = values().find { it.value == value } ?: INPROGRESS
    }
}