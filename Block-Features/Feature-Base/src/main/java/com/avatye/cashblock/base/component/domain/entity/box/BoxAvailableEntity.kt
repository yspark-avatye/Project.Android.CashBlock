package com.avatye.cashblock.base.component.domain.entity.box

data class BoxAvailableEntity(
    val isAvailable: Boolean = false,
    val showInterstitial: Boolean = false,
    val needAgeVerification: Boolean = false
)
