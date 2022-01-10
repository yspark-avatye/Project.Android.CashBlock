package com.avatye.cashblock.base.internal.server.entity.game

import android.graphics.Color
import com.avatye.cashblock.base.MODULE_NAME
import com.avatye.cashblock.base.component.domain.entity.game.GameEntity
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.base.library.miscellaneous.toIntValue
import com.avatye.cashblock.base.library.miscellaneous.toStringValue
import com.avatye.cashblock.base.library.miscellaneous.until
import com.avatye.cashblock.base.internal.server.serve.ServeSuccess
import org.json.JSONArray

class ResGameList : ServeSuccess() {
    val gameEntities: MutableList<GameEntity> = mutableListOf()

    override fun makeBody(responseValue: String) {
        JSONArray(responseValue).let {
            it.until { item ->
                val textColor = item.toStringValue("textColor", "#212121")
                val convertedColor = if (textColor.contains("#")) textColor else "#$textColor"
                gameEntities.add(GameEntity(
                    gameID = item.toStringValue("gameID"),
                    title = item.toStringValue("title"),
                    useTicketAmount = item.toIntValue("useTicketAmount"),
                    iconUrl = item.toStringValue("iconUrl"),
                    imageUrl = item.toStringValue("imageUrl"),
                    titleTextColor = try {
                        Color.parseColor(convertedColor)
                    } catch (e: Exception) {
                        LogHandler.e(moduleName = MODULE_NAME, throwable = e) {
                            "GameItem -> parseTextColor"
                        }
                        Color.parseColor("#212121")
                    }
                ))
            }
        }
    }
}