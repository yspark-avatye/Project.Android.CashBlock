package com.avatye.cashblock.feature.roulette.presentation.viewmodel.roulette

import androidx.lifecycle.*
import com.avatye.cashblock.base.component.contract.data.RouletteDataContract
import com.avatye.cashblock.base.component.domain.entity.game.GameEntity
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult
import com.avatye.cashblock.feature.roulette.RouletteConfig

internal class RouletteListViewModel : ViewModel() {
    internal companion object {
        fun create(viewModelStoreOwner: ViewModelStoreOwner): RouletteListViewModel {
            return ViewModelProvider(viewModelStoreOwner).get(RouletteListViewModel::class.java)
        }
    }

    private val apiContract: RouletteDataContract by lazy {
        RouletteDataContract(blockCode = RouletteConfig.blockCode)
    }

    // region { view model data }
    private val _result = MutableLiveData<ViewModelResult<MutableList<GameEntity>>>()
    val result: LiveData<ViewModelResult<MutableList<GameEntity>>> get() = _result
    // endregion

    fun request() {
        _result.value = Contract.postInProgress()
        apiContract.retrieveList {
            when (it) {
                is ContractResult.Success -> _result.value = Contract.postComplete(it.contract)
                is ContractResult.Failure -> _result.value = Contract.postError(it)
            }
        }
    }
}