package com.avatye.cashblock.base.component.domain.entity.ticket

data class TicketRequestEntity(
    val transactionId: String = "",
    val needAgeVerification: Boolean = false
)