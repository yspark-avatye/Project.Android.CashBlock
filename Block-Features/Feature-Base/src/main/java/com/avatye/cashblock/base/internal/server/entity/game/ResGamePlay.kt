package com.avatye.cashblock.base.internal.server.entity.game

import com.avatye.cashblock.base.component.domain.entity.game.GamePlayEntity
import com.avatye.cashblock.base.library.miscellaneous.toBooleanValue
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import org.json.JSONObject

class ResGamePlay : ServeSuccess() {
    var gamePlayEntity = GamePlayEntity()
        private set

    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            gamePlayEntity = GamePlayEntity(
                participateID = it.toStringValue("participateID"),
                isDisplayBoard = it.toBooleanValue("isDisplayBoard"),
                rewardText = it.toStringValue("rewardText")
            )
        }
    }
}