package com.avatye.cashblock.base.internal.server

import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.internal.server.entity.ResVoid
import com.avatye.cashblock.base.internal.server.entity.game.ResGameList
import com.avatye.cashblock.base.internal.server.entity.game.ResGamePlay
import com.avatye.cashblock.base.internal.server.entity.game.ResGameView
import com.avatye.cashblock.base.internal.server.entity.game.ResGameWinnerBoard
import com.avatye.cashblock.base.internal.server.serve.IServeToken
import com.avatye.cashblock.base.internal.server.serve.ServeResponse
import com.avatye.cashblock.base.internal.server.serve.ServeTask

object APIGame {
    /**
     * get 'roulette' game info
     */
    fun getGameView(blockCode: BlockCode, tokenizer: IServeToken, gameId: String, response: ServeResponse<ResGameView>) {
        ServeTask(
            blockCode = blockCode,
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
    fun getGameList(blockCode: BlockCode, tokenizer: IServeToken, response: ServeResponse<ResGameList>) {
        ServeTask(
            blockCode = blockCode,
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
    fun postGamePlay(blockCode: BlockCode, tokenizer: IServeToken, gameId: String, response: ServeResponse<ResGamePlay>) {
        ServeTask(
            blockCode = blockCode,
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
    fun postGameWinBoard(blockCode: BlockCode, tokenizer: IServeToken, participateId: String, message: String, response: ServeResponse<ResVoid>) {
        ServeTask(
            blockCode = blockCode,
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
    fun getWinnerBoard(blockCode: BlockCode, tokenizer: IServeToken, response: ServeResponse<ResGameWinnerBoard>) {
        ServeTask(
            blockCode = blockCode,
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