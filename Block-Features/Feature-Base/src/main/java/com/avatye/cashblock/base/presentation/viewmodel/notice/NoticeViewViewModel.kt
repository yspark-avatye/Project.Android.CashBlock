package com.avatye.cashblock.base.presentation.viewmodel.notice

import androidx.lifecycle.*
import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.component.contract.data.SupportDataContract
import com.avatye.cashblock.base.component.domain.entity.support.NoticeEntity
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult

internal class NoticeViewViewModel(val blockCode: BlockCode) : ViewModel() {

    companion object {
        fun create(blockCode: BlockCode, viewModelStoreOwner: ViewModelStoreOwner): NoticeViewViewModel {
            return ViewModelProvider(viewModelStoreOwner, Factory(blockCode = blockCode)).get(NoticeViewViewModel::class.java)
        }
    }

    class Factory(private val blockCode: BlockCode) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(NoticeViewViewModel::class.java)) {
                NoticeViewViewModel(blockCode = blockCode) as T
            } else {
                throw IllegalArgumentException("NoticeViewViewModel must have parameter(blockCode: BlockCode)")
            }
        }
    }

    private val apiContract: SupportDataContract by lazy {
        SupportDataContract(blockCode = blockCode)
    }

    private val _result = MutableLiveData<ViewModelResult<NoticeEntity>>()
    val result: LiveData<ViewModelResult<NoticeEntity>> = _result

    fun request(noticeId: String) {
        _result.value = ViewModelResult.InProgress
        apiContract.retrieveNoticeView(noticeId = noticeId) {
            when (it) {
                is ContractResult.Success -> _result.value = Contract.postComplete(it.contract)
                is ContractResult.Failure -> _result.value = Contract.postError(it)
            }
        }
    }
}