package com.avatye.cashblock.base.internal.server.entity.box

import com.avatye.cashblock.base.component.domain.entity.box.BoxUseEntity
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import org.json.JSONObject

internal class ResBoxUse : ServeSuccess() {
    var boxUse = BoxUseEntity()
        private set

    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            boxUse = BoxUseEntity(
                rewardText = it.toStringValue("rewardText", "")
            )
        }
    }
}