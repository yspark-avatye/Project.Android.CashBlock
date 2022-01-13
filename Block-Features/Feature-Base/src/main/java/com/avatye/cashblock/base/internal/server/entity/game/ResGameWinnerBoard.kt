package com.avatye.cashblock.base.internal.server.entity.game

import com.avatye.cashblock.base.component.domain.entity.game.WinnerMessageEntity
import com.avatye.cashblock.base.library.miscellaneous.toDateTimeValue
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import com.avatye.cashblock.base.library.miscellaneous.until
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import org.json.JSONArray

internal class ResGameWinnerBoard : ServeSuccess() {
    val winnerMessageEntities: MutableList<WinnerMessageEntity> = mutableListOf()

    override fun makeBody(responseValue: String) {
        JSONArray(responseValue).let {
            it.until { item ->
                winnerMessageEntities.add(
                    WinnerMessageEntity(
                        participateID = item.toStringValue("participateID"),
                        appID = item.toStringValue("appID"),
                        message = item.toStringValue("message"),
                        rewardText = item.toStringValue("rewardText"),
                        winDateTime = item.toDateTimeValue("winDateTime")
                    )
                )
            }
        }
    }
}