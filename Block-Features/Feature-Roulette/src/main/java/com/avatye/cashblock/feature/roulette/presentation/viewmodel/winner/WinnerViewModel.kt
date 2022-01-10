package com.avatye.cashblock.feature.roulette.presentation.viewmodel.winner

import androidx.lifecycle.*
import com.avatye.cashblock.base.component.contract.data.RouletteDataContract
import com.avatye.cashblock.base.component.domain.entity.game.WinnerMessageEntity
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.component.model.entity.winner.WinnerItemEntity

internal class WinnerViewModel : ViewModel() {
    internal companion object {
        fun create(viewModelStoreOwner: ViewModelStoreOwner): WinnerViewModel {
            return ViewModelProvider(viewModelStoreOwner).get(WinnerViewModel::class.java)
        }
    }

    private val apiContract: RouletteDataContract by lazy {
        RouletteDataContract(blockCode = RouletteConfig.blockCode)
    }

    private val _contents = MutableLiveData<ViewModelResult<MutableList<WinnerItemEntity>>>()
    val contents: LiveData<ViewModelResult<MutableList<WinnerItemEntity>>> get() = _contents

    fun request() {
        apiContract.retrieveWinnerBoardList {
            when (it) {
                is ContractResult.Success -> _contents.value = modelConvert(models = Contract.postComplete(it.contract).result)
                is ContractResult.Failure -> _contents.value = Contract.postError(it)
            }
        }
    }

    fun add(participateId: String, winnerMessage: String, callback: (ViewModelResult<Boolean>) -> Unit) {
        callback(Contract.postInProgress())
        apiContract.postWinnerBoard(participateId = participateId, message = winnerMessage) {
            when (it) {
                is ContractResult.Success -> callback(Contract.postComplete(true))
                is ContractResult.Failure -> callback(Contract.postError(it))
            }
        }
    }

    private fun modelConvert(models: MutableList<WinnerMessageEntity>): ViewModelResult.Complete<MutableList<WinnerItemEntity>> {
        val result: MutableList<WinnerItemEntity> = mutableListOf()
        models.forEach {
            result.add(WinnerItemEntity(message = it.message, rewardText = it.rewardText, createDateTime = it.winDateTime))
        }
        return Contract.postComplete(result)
    }
}