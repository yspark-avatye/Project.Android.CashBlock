package com.avatye.cashblock.feature.roulette.presentation.viewmodel.box

import androidx.lifecycle.*
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.api.BoxApiContractor
import com.avatye.cashblock.base.component.contract.business.EventContractor
import com.avatye.cashblock.base.component.domain.entity.box.BoxAvailableEntity
import com.avatye.cashblock.base.component.domain.entity.box.BoxType
import com.avatye.cashblock.base.component.domain.entity.box.BoxUseEntity
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.component.controller.TicketBoxController

internal class TicketBoxViewModel : ViewModel() {
    internal companion object {
        fun create(viewModelStoreOwner: ViewModelStoreOwner): TicketBoxViewModel {
            return ViewModelProvider(viewModelStoreOwner).get(TicketBoxViewModel::class.java)
        }
    }

    private val tagName = "TicketBoxViewModel"
    private val api: BoxApiContractor by lazy {
        BoxApiContractor(blockType = RouletteConfig.blockType)
    }

    // region { view model data: available }
    private val _boxAvailable = MutableLiveData<ViewModelResult<BoxAvailableEntity>>()
    val boxAvailable: LiveData<ViewModelResult<BoxAvailableEntity>> get() = _boxAvailable

    fun requestBoxAvailable() {
        _boxAvailable.value = Contract.postInProgress()
        api.retrieveAvailable(boxType = BoxType.TICKET_BOX) {
            when (it) {
                is ContractResult.Failure -> _boxAvailable.value = Contract.postError(it)
                is ContractResult.Success -> {
                    _boxAvailable.value = Contract.postComplete(it.contract)
                    if (!it.contract.isAvailable) {
                        TicketBoxController.updateComplete(isComplete = true)
                    }
                }
            }
        }
    }
    // endregion

    // region { view model data: reward }
    private val _boxUse = MutableLiveData<ViewModelResult<BoxUseEntity>>()
    val boxUse: LiveData<ViewModelResult<BoxUseEntity>> get() = _boxUse

    fun requestBoxUse() {
        _boxUse.value = Contract.postInProgress()
        api.postUse(boxType = BoxType.TICKET_BOX) {
            when (it) {
                is ContractResult.Failure -> _boxUse.value = Contract.postError(it)
                is ContractResult.Success -> {
                    _boxUse.value = Contract.postComplete(it.contract)
                    TicketBoxController.updateComplete(isComplete = true)
                    EventContractor.postTicketBoxUpdate(blockType = BlockType.ROULETTE)
                }
            }
        }
    }
    // endregion
}