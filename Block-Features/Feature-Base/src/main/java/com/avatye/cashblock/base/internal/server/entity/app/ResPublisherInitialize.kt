package com.avatye.cashblock.base.internal.server.entity.app

import com.avatye.cashblock.base.component.domain.entity.app.PublisherInitializeEntity
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import com.avatye.cashblock.base.library.miscellaneous.toIntValue
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import org.json.JSONObject

class ResPublisherInitialize : ServeSuccess() {
    var publisher = PublisherInitializeEntity()
        private set

    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            publisher = PublisherInitializeEntity(
                code = it.toIntValue("code", -1),
                appID = it.toStringValue("appID", ""),
                appSecret = it.toStringValue("appSecret", ""),
                retryHours = it.toIntValue("retryHours", 0)
            )
        }
    }
}