package com.avatye.cashblock.base.component.domain.entity.app

import org.joda.time.DateTime

data class AppInspection(
    val fromDateTime: DateTime? = null,
    val toDateTime: DateTime? = null,
    val message: String,
    val link: String
)