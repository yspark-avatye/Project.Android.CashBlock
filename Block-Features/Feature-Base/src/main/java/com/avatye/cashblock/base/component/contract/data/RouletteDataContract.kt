package com.avatye.cashblock.base.component.contract.data

import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.component.contract.AccountContract
import com.avatye.cashblock.base.component.domain.entity.game.GameEntity
import com.avatye.cashblock.base.component.domain.entity.game.GamePlayEntity
import com.avatye.cashblock.base.component.domain.entity.game.WinnerMessageEntity
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.internal.server.APIGame
import com.avatye.cashblock.base.internal.server.entity.ResVoid
import com.avatye.cashblock.base.internal.server.entity.game.ResGameList
import com.avatye.cashblock.base.internal.server.entity.game.ResGamePlay
import com.avatye.cashblock.base.internal.server.entity.game.ResGameView
import com.avatye.cashblock.base.internal.server.entity.game.ResGameWinnerBoard
import com.avatye.cashblock.base.internal.server.serve.IServeToken
import com.avatye.cashblock.base.internal.server.serve.ServeFailure
import com.avatye.cashblock.base.internal.server.serve.ServeResponse

class RouletteDataContract(private val blockCode: BlockCode) {
    private val appId = blockCode.blockId

    private val tokenizer = object : IServeToken {
        override fun makeBasicToken() = blockCode.basicToken
        override fun makeBearerToken() = AccountContract.accessToken
    }

    fun retrieveView(gameId: String, response: (contract: ContractResult<GameEntity>) -> Unit) {
        APIGame.getGameView(appId = appId, tokenizer = tokenizer, gameId = gameId, response = object : ServeResponse<ResGameView> {
            override fun onSuccess(success: ResGameView) = response(Contract.onSuccess(success.gameEntity))
            override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
        })
    }

    fun retrieveList(response: (contract: ContractResult<MutableList<GameEntity>>) -> Unit) {
        APIGame.getGameList(appId = appId, tokenizer = tokenizer, response = object : ServeResponse<ResGameList> {
            override fun onSuccess(success: ResGameList) = response(Contract.onSuccess(success.gameEntities))
            override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
        })
    }

    fun postPlay(gameId: String, response: (contract: ContractResult<GamePlayEntity>) -> Unit) {
        APIGame.postGamePlay(appId = appId, tokenizer = tokenizer, gameId = gameId, response = object : ServeResponse<ResGamePlay> {
            override fun onSuccess(success: ResGamePlay) = response(Contract.onSuccess(success.gamePlayEntity))
            override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
        })
    }

    fun postWinnerBoard(participateId: String, message: String, response: (contract: ContractResult<Unit>) -> Unit) {
        APIGame.postGameWinBoard(appId = appId, tokenizer = tokenizer, participateId = participateId, message = message, response = object : ServeResponse<ResVoid> {
            override fun onSuccess(success: ResVoid) = response(Contract.onSuccess(Unit))
            override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
        })
    }

    fun retrieveWinnerBoardList(response: (contract: ContractResult<MutableList<WinnerMessageEntity>>) -> Unit) {
        APIGame.getWinnerBoard(appId = appId, tokenizer = tokenizer, response = object : ServeResponse<ResGameWinnerBoard> {
            override fun onSuccess(success: ResGameWinnerBoard) = response(Contract.onSuccess(success.winnerMessageEntities))
            override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
        })
    }
}