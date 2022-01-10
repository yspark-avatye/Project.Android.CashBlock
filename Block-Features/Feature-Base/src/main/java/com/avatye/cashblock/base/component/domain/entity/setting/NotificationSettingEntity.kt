package com.avatye.cashblock.base.component.domain.entity.setting

data class NotificationSettingEntity(
    val inducePeriod: Long,
    val induceTicketCount: Int
) {
    companion object {
        fun empty() = NotificationSettingEntity(
            inducePeriod = (((60L * 60L) * 24L) * 30L), //30-days
            induceTicketCount = 5
        )
    }
}