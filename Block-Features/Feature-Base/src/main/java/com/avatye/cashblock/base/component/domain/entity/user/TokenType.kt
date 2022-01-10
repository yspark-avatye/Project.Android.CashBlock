package com.avatye.cashblock.base.component.domain.entity.user

enum class TokenType(val value: String) {
    BASIC("basic"), BEARER("bearer");

    companion object {
        fun from(value: String): TokenType {
            return if (value.equals(other = "bearer", ignoreCase = true)) {
                BEARER
            } else {
                BASIC
            }
        }
    }
}