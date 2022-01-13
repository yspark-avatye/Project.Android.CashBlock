package com.avatye.cashblock.base.component.domain.entity.user

data class Profile(
    val userId: String,
    val birthYear: Int,
    val gender: GenderType
)
