package com.avatye.cashblock.base.internal.server.entity.support

import com.avatye.cashblock.base.component.domain.entity.support.NoticeEntity
import com.avatye.cashblock.base.library.miscellaneous.*
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import org.json.JSONArray

internal class ResNoticeList : ServeSuccess() {
    val noticeEntities: MutableList<NoticeEntity> = mutableListOf()

    override fun makeBody(responseValue: String) {
        JSONArray(responseValue).until {
            noticeEntities.add(
                NoticeEntity(
                    noticeID = it.toStringValue("noticeID"),
                    subject = it.toStringValue("subject"),
                    body = null,
                    readCount = it.toIntValue("readCount"),
                    displayTop = it.toBooleanValue("displayTop"),
                    noticeDateTime = it.toDateTimeValue("noticeDateTime"),
                    lastUpdateDateTime = it.toDateTimeValue("lastUpdateDateTime")
                )
            )
        }
    }
}