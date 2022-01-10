package com.avatye.cashblock.base.component.domain.entity.user

enum class AgeVerifiedType(val value: Int) {

    NONE(-1), VERIFIED(1), UNVERIFIED(0);

    @Override
    fun equals(other: Int): Boolean = this.value == other

    companion object {
        fun from(value: Int): AgeVerifiedType {
            return when (value) {
                1 -> VERIFIED
                0 -> UNVERIFIED
                else -> NONE
            }
        }

        fun from(value: Boolean): AgeVerifiedType {
            return when (value) {
                true -> VERIFIED
                false -> UNVERIFIED
            }
        }
    }

}