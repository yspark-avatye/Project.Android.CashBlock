package com.avatye.cashblock.base.internal.server.entity.support

import com.avatye.cashblock.base.component.domain.entity.support.NoticeEntity
import com.avatye.cashblock.base.library.miscellaneous.toBooleanValue
import com.avatye.cashblock.base.library.miscellaneous.toDateTimeValue
import com.avatye.cashblock.base.library.miscellaneous.toIntValue
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import org.json.JSONObject

internal class ResNotice : ServeSuccess() {
    var noticeEntity = NoticeEntity()
        private set

    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            noticeEntity = NoticeEntity(
                noticeID = null,
                subject = it.toStringValue("subject"),
                body = it.toStringValue("body"),
                readCount = it.toIntValue("readCount"),
                displayTop = it.toBooleanValue("displayTop"),
                noticeDateTime = it.toDateTimeValue("noticeDateTime"),
                lastUpdateDateTime = it.toDateTimeValue("lastUpdateDateTime")
            )
        }
    }
}