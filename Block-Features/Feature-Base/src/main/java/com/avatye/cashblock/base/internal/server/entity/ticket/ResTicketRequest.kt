package com.avatye.cashblock.base.internal.server.entity.ticket

import com.avatye.cashblock.base.component.domain.entity.ticket.TicketRequestEntity
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import com.avatye.cashblock.base.library.miscellaneous.toBooleanValue
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import org.json.JSONObject

internal class ResTicketRequest : ServeSuccess() {
    var ticketRequestEntity = TicketRequestEntity()
        private set

    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            ticketRequestEntity = TicketRequestEntity(
                transactionId = it.toStringValue("ticketTransactionID"),
                needAgeVerification = it.toBooleanValue("needAgeVerification")
            )
        }
    }
}