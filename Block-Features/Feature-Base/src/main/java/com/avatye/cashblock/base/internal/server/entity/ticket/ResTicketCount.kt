package com.avatye.cashblock.base.internal.server.entity.ticket

import com.avatye.cashblock.base.component.domain.entity.ticket.TicketCountEntity
import com.avatye.cashblock.base.library.miscellaneous.toIntValue
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import org.json.JSONObject

internal class ResTicketCount : ServeSuccess() {
    var ticketCountEntity = TicketCountEntity()
        private set

    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            val receiveCount = it.toIntValue("receiveCount", 0)
            val limitCount = it.toIntValue("limitCount", 0)
            ticketCountEntity = TicketCountEntity(
                receiveCount = receiveCount,
                limitCount = limitCount,
                receivableCount = limitCount - receiveCount
            )
        }
    }
}