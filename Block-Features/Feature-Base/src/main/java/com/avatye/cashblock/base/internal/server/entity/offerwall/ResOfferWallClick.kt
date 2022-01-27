package com.avatye.cashblock.base.internal.server.entity.offerwall

import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallClickEntity
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import org.json.JSONObject

internal class ResOfferWallClick : ServeSuccess() {
    var offerwallClickEntity = OfferwallClickEntity()
        private set

    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            offerwallClickEntity = OfferwallClickEntity(
                clickID = it.toStringValue("clickID"),
                landingUrl = it.toStringValue("landingUrl")
            )
        }
    }
}