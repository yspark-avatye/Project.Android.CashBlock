package com.avatye.cashblock.base.internal.server.entity.box

import com.avatye.cashblock.base.component.domain.entity.box.BoxAvailableEntity
import com.avatye.cashblock.base.library.miscellaneous.toBooleanValue
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import org.json.JSONObject

internal class ResBoxAvailable : ServeSuccess() {
    var boxAvailable = BoxAvailableEntity()
        private set

    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            boxAvailable = BoxAvailableEntity(
                isAvailable = it.toBooleanValue("isAvailable"),
                showInterstitial = it.toBooleanValue("showInterstitial"),
                needAgeVerification = it.toBooleanValue("needAgeVerification")
            )
        }
    }
}