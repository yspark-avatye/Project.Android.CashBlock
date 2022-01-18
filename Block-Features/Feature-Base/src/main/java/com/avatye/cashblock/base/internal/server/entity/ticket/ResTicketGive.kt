package com.avatye.cashblock.base.internal.server.entity.ticket

import com.avatye.cashblock.base.component.domain.entity.ticket.TicketBalanceEntity
import com.avatye.cashblock.base.library.miscellaneous.toIntValue
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import org.json.JSONObject

internal class ResTicketGive : ServeSuccess() {
    var ticketBalanceEntity = TicketBalanceEntity()
        private set

    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            ticketBalanceEntity = TicketBalanceEntity(
                balance = it.toIntValue("balance")
            )
        }
    }
}