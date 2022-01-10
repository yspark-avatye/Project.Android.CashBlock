package com.avatye.cashblock.base.internal.server.entity.app

import com.avatye.cashblock.base.component.domain.entity.setting.SettingEntity
import com.avatye.cashblock.base.library.miscellaneous.toLongValue
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import org.json.JSONObject

class ResSettings : ServeSuccess() {
    var settings = SettingEntity()
        private set

    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            settings = SettingEntity(
                updateDateTime = it.toLongValue("updateDateTime", 0L),
                settings = it
            )
        }
    }
}