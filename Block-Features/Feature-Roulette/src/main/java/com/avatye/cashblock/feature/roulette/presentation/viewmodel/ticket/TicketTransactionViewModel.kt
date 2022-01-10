package com.avatye.cashblock.feature.roulette.presentation.viewmodel.ticket

import androidx.lifecycle.*
import com.avatye.cashblock.base.component.contract.data.TicketDataContract
import com.avatye.cashblock.base.component.domain.entity.ticket.TicketBalanceEntity
import com.avatye.cashblock.base.component.domain.entity.ticket.TicketRequestEntity
import com.avatye.cashblock.base.component.domain.entity.ticket.TicketType
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult
import com.avatye.cashblock.feature.roulette.RouletteConfig

internal class TicketTransactionViewModel(private val ticketType: TicketType) : ViewModel() {
    companion object {
        fun create(ticketType: TicketType, viewModelStoreOwner: ViewModelStoreOwner): TicketTransactionViewModel {
            return ViewModelProvider(
                viewModelStoreOwner,
                Factory(ticketType = ticketType)
            ).get(TicketTransactionViewModel::class.java)
        }
    }

    class Factory(private val ticketType: TicketType) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(TicketTransactionViewModel::class.java)) {
                TicketTransactionViewModel(ticketType = ticketType) as T
            } else {
                throw IllegalArgumentException("TicketTransactionViewModel must have parameter(ticketType: TicketType)")
            }
        }
    }

    private val apiContract: TicketDataContract by lazy {
        TicketDataContract(blockCode = RouletteConfig.blockCode)
    }

    // region # local
    private var _transactionId: String? = null
    // endregion

    // region # ticket transaction
    private val _transaction = MutableLiveData<ViewModelResult<TicketRequestEntity>>()
    val transaction: LiveData<ViewModelResult<TicketRequestEntity>> get() = _transaction

    fun requestTransaction() {
        _transactionId = ""
        _transaction.value = Contract.postInProgress()
        apiContract.postTransaction(ticketType = ticketType) {
            when (it) {
                is ContractResult.Success -> {
                    _transactionId = it.contract.transactionId
                    _transaction.value = Contract.postComplete(it.contract)
                }
                is ContractResult.Failure -> {
                    _transaction.value = Contract.postError(it)
                }
            }
        }
    }
    // endregion


    // region # ticket ticketing
    private val _ticketing = MutableLiveData<ViewModelResult<TicketBalanceEntity>>()
    val ticketing: LiveData<ViewModelResult<TicketBalanceEntity>> get() = _ticketing
    fun requestTicketing() {
        _transactionId?.let { tid ->
            _ticketing.value = Contract.postInProgress()
            apiContract.postTicketing(ticketType = ticketType, transactionId = tid) {
                when (it) {
                    is ContractResult.Success -> _ticketing.value = Contract.postComplete(it.contract)
                    is ContractResult.Failure -> _ticketing.value = Contract.postError(it)
                }
            }
        } ?: run {
            _ticketing.value = ViewModelResult.Error(statusCode = 0, errorCode = "", message = "")
        }
    }
    // endregion
}