package com.avatye.cashblock.base.component.domain.entity.user

enum class GenderType(val value: String) {
    MALE("M"), FEMALE("F");

    @Override
    fun equals(other: String): Boolean = this.value.equals(other = other, ignoreCase = true)

    internal companion object {
        fun from(value: String): GenderType? {
            return values().find {
                it.value.equals(other = value, ignoreCase = true)
            }
        }
    }
}