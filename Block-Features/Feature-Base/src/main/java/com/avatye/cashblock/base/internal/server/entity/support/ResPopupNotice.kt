package com.avatye.cashblock.base.internal.server.entity.support

import com.avatye.cashblock.base.component.domain.entity.support.PopupDisplayType
import com.avatye.cashblock.base.component.domain.entity.support.PopupNoticeEntity
import com.avatye.cashblock.base.library.miscellaneous.toIntValue
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import com.avatye.cashblock.base.library.miscellaneous.until
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import org.json.JSONArray

internal class ResPopupNotice : ServeSuccess() {
    val popupNoticeEntities: MutableList<PopupNoticeEntity> = mutableListOf()

    override fun makeBody(responseValue: String) {
        JSONArray(responseValue).until {
            popupNoticeEntities.add(
                PopupNoticeEntity(
                    popupID = it.toStringValue("popupID"),
                    title = it.toStringValue("title"),
                    imageUrl = it.toStringValue("imageUrl"),
                    landingName = it.toStringValue("landing"),
                    landingValue = it.toStringValue("landingDetail"),
                    displayType = PopupDisplayType.from(it.toIntValue("displayType", 1))
                )
            )
        }
    }
}