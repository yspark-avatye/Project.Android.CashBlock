package com.avatye.cashblock.base.component.domain.entity.support

import org.joda.time.DateTime

data class NoticeEntity(
    val noticeID: String? = null,
    val subject: String = "",
    val body: String? = null,
    val readCount: Int = 0,
    val displayTop: Boolean = false,
    val noticeDateTime: DateTime? = null,
    val lastUpdateDateTime: DateTime? = null
)