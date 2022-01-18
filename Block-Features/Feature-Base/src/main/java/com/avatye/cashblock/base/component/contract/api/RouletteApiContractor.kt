package com.avatye.cashblock.base.component.contract.api

import com.avatye.cashblock.base.block.BlockType
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
import com.avatye.cashblock.base.internal.server.serve.ServeFailure
import com.avatye.cashblock.base.internal.server.serve.ServeResponse

class RouletteApiContractor(private val blockType: BlockType) {

    fun retrieveView(gameId: String, response: (contract: ContractResult<GameEntity>) -> Unit) {
        APIGame.getGameView(
            blockType = blockType,
            gameId = gameId,
            response = object : ServeResponse<ResGameView> {
                override fun onSuccess(success: ResGameView) = response(Contract.onSuccess(success.gameEntity))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }

    fun retrieveList(response: (contract: ContractResult<MutableList<GameEntity>>) -> Unit) {
        APIGame.getGameList(
            blockType = blockType,
            response = object : ServeResponse<ResGameList> {
                override fun onSuccess(success: ResGameList) = response(Contract.onSuccess(success.gameEntities))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }

    fun postPlay(gameId: String, response: (contract: ContractResult<GamePlayEntity>) -> Unit) {
        APIGame.postGamePlay(
            blockType = blockType,
            gameId = gameId,
            response = object : ServeResponse<ResGamePlay> {
                override fun onSuccess(success: ResGamePlay) = response(Contract.onSuccess(success.gamePlayEntity))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }

    fun postWinnerBoard(participateId: String, message: String, response: (contract: ContractResult<Unit>) -> Unit) {
        APIGame.postGameWinBoard(
            blockType = blockType,
            participateId = participateId,
            message = message,
            response = object : ServeResponse<ResVoid> {
                override fun onSuccess(success: ResVoid) = response(Contract.onSuccess(Unit))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }

    fun retrieveWinnerBoardList(response: (contract: ContractResult<MutableList<WinnerMessageEntity>>) -> Unit) {
        APIGame.getWinnerBoard(
            blockType = blockType,
            response = object : ServeResponse<ResGameWinnerBoard> {
                override fun onSuccess(success: ResGameWinnerBoard) = response(Contract.onSuccess(success.winnerMessageEntities))
                override fun onFailure(failure: ServeFailure) = response(Contract.onFailure(failure))
            })
    }
}