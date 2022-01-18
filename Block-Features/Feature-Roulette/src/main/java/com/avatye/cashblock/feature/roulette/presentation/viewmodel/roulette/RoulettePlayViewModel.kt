package com.avatye.cashblock.feature.roulette.presentation.viewmodel.roulette

import androidx.lifecycle.*
import com.avatye.cashblock.base.component.contract.api.RouletteApiContractor
import com.avatye.cashblock.base.component.domain.entity.game.GamePlayEntity
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult
import com.avatye.cashblock.feature.roulette.RouletteConfig


internal class RoulettePlayViewModel : ViewModel() {
    internal companion object {
        fun create(viewModelStoreOwner: ViewModelStoreOwner): RoulettePlayViewModel {
            return ViewModelProvider(viewModelStoreOwner).get(RoulettePlayViewModel::class.java)
        }
    }

    private val api: RouletteApiContractor by lazy {
        RouletteApiContractor(blockType = RouletteConfig.blockType)
    }

    // region { view model data }
    private val _playResult = MutableLiveData<ViewModelResult<GamePlayEntity>>()
    val playResult: LiveData<ViewModelResult<GamePlayEntity>> get() = _playResult
    // endregion

    fun play(rouletteId: String) {
        _playResult.value = Contract.postInProgress()
        api.postPlay(gameId = rouletteId) {
            when (it) {
                is ContractResult.Success -> _playResult.value = Contract.postComplete(it.contract)
                is ContractResult.Failure -> _playResult.value = Contract.postError(it)
            }
        }
    }
}