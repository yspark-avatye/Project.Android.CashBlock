package com.avatye.cashblock.base.component.domain.entity.setting

import org.json.JSONObject

data class SettingEntity(
    val updateDateTime: Long = 0L,
    val settings: JSONObject? = null
)