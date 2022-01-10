package com.avatye.cashblock.base.internal.server

import com.avatye.cashblock.base.internal.server.entity.ResVoid
import com.avatye.cashblock.base.internal.server.entity.game.ResGameList
import com.avatye.cashblock.base.internal.server.entity.game.ResGamePlay
import com.avatye.cashblock.base.internal.server.entity.game.ResGameView
import com.avatye.cashblock.base.internal.server.entity.game.ResGameWinnerBoard
import com.avatye.cashblock.base.internal.server.serve.IServeToken
import com.avatye.cashblock.base.internal.server.serve.ServeTask
import com.avatye.cashblock.base.internal.server.serve.ServeResponse

object APIGame {
    /**
     * get 'roulette' game info
     */
    fun getGameView(appId: String, tokenizer: IServeToken, gameId: String, response: ServeResponse<ResGameView>) {
        ServeTask(
            appId = appId,
            authorization = ServeTask.Authorization.BEARER,
            tokenizer = tokenizer,
            method = ServeTask.Method.GET,
            requestUrl = "game",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf("gameID" to gameId),
            responseClass = ResGameView::class.java,
            responseCallback = response
        ).execute()
    }

    /**
     * get 'roulette' game list
     */
    fun getGameList(appId: String, tokenizer: IServeToken, response: ServeResponse<ResGameList>) {
        ServeTask(
            appId = appId,
            authorization = ServeTask.Authorization.BEARER,
            tokenizer = tokenizer,
            method = ServeTask.Method.GET,
            requestUrl = "games",
            acceptVersion = "1.0.0",
            responseClass = ResGameList::class.java,
            responseCallback = response
        ).execute()
    }

    /**
     * participate 'roulette' game
     */
    fun postGamePlay(appId: String, tokenizer: IServeToken, gameId: String, response: ServeResponse<ResGamePlay>) {
        ServeTask(
            appId = appId,
            authorization = ServeTask.Authorization.BEARER,
            tokenizer = tokenizer,
            method = ServeTask.Method.POST,
            requestUrl = "game/play",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf("gameID" to gameId),
            responseClass = ResGamePlay::class.java,
            responseCallback = response
        ).execute()
    }

    /**
     * insert 'roulette' winner message
     */
    fun postGameWinBoard(appId: String, tokenizer: IServeToken, participateId: String, message: String, response: ServeResponse<ResVoid>) {
        ServeTask(
            appId = appId,
            authorization = ServeTask.Authorization.BEARER,
            tokenizer = tokenizer,
            method = ServeTask.Method.POST,
            requestUrl = "game/winBoard",
            acceptVersion = "1.0.0",
            argsBody = hashMapOf(
                "participateID" to participateId,
                "message" to message
            ),
            responseClass = ResVoid::class.java,
            responseCallback = response
        ).execute()
    }

    /**
     * get 'roulette' winner messages
     */
    fun getWinnerBoard(appId: String, tokenizer: IServeToken, response: ServeResponse<ResGameWinnerBoard>) {
        ServeTask(
            appId = appId,
            authorization = ServeTask.Authorization.BEARER,
            tokenizer = tokenizer,
            method = ServeTask.Method.GET,
            requestUrl = "game/winBoard",
            acceptVersion = "1.0.0",
            responseClass = ResGameWinnerBoard::class.java,
            responseCallback = response
        ).execute()
    }

}