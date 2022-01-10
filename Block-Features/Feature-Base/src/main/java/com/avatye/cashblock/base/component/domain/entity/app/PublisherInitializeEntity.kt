package com.avatye.cashblock.base.component.domain.entity.app

data class PublisherInitializeEntity(
    val code: Int = -1,
    val appID: String = "",
    val appSecret: String = "",
    val retryHours: Int = 0
) {
    val isSuccess: Boolean
        get() {
            return code == 0
        }

    val isPublisherExpired: Boolean
        get() {
            return code == 100
        }
}