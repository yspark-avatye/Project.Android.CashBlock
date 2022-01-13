package com.avatye.cashblock.base.internal.server.entity.game

import android.graphics.Color
import com.avatye.cashblock.base.MODULE_NAME
import com.avatye.cashblock.base.component.domain.entity.game.GameEntity
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.base.library.miscellaneous.toIntValue
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import org.json.JSONObject

internal class ResGameView : ServeSuccess() {
    var gameEntity = GameEntity()
        private set

    override fun makeBody(responseValue: String) {
        JSONObject(responseValue).let {
            val textColor = it.toStringValue("textColor", "#212121")
            val convertedColor = if (textColor.contains("#")) textColor else "#$textColor"
            gameEntity = GameEntity(
                gameID = it.toStringValue("gameID"),
                title = it.toStringValue("title"),
                useTicketAmount = it.toIntValue("useTicketAmount"),
                iconUrl = it.toStringValue("iconUrl"),
                imageUrl = it.toStringValue("imageUrl"),
                titleTextColor = try {
                    Color.parseColor(convertedColor)
                } catch (e: Exception) {
                    LogHandler.e(moduleName = MODULE_NAME, throwable = e) {
                        "GameItem -> parseTextColor"
                    }
                    Color.parseColor("#212121")
                }
            )
        }
    }
}