package com.avatye.cashblock.base.component.domain.entity.user

data class LoginEntity(
    val sdkUserID: String = "",
    val ageVerifiedType: AgeVerifiedType = AgeVerifiedType.NONE,
    val accessToken: String = "",
    val tokenType: TokenType? = null
)