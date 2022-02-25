package com.avatye.cashblock.base.internal.server.entity.offerwall

import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallTabEntity
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import com.avatye.cashblock.base.library.miscellaneous.toIntValue
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import com.avatye.cashblock.base.library.miscellaneous.until
import org.json.JSONArray

internal class ResOfferwallTabs: ServeSuccess() {

    val tabEntities: MutableList<OfferwallTabEntity> = mutableListOf()

    override fun makeBody(responseValue: String) {
        JSONArray(responseValue).until {
            tabEntities.add(
                OfferwallTabEntity(
                    tabID = it.toStringValue("tabID"),
                    tabName = it.toStringValue("tabName"),
                    listType = it.toIntValue("listType"),
                    sortOrder = it.toIntValue("sortOrder"),
                    sectionIDList = it.toStringValue("sectionList").split(","),
                )
            )
        }
    }
}