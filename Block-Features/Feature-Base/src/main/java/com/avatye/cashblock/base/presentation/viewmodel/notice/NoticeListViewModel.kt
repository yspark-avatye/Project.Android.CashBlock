package com.avatye.cashblock.base.presentation.viewmodel.notice

import androidx.lifecycle.*
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.api.SupportApiContractor
import com.avatye.cashblock.base.component.domain.entity.support.NoticeEntity
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult

internal class NoticeListViewModel(val blockType: BlockType) : ViewModel() {

    companion object {
        fun create(blockType: BlockType, viewModelStoreOwner: ViewModelStoreOwner): NoticeListViewModel {
            return ViewModelProvider(viewModelStoreOwner, Factory(blockType = blockType)).get(NoticeListViewModel::class.java)
        }
    }

    class Factory(private val blockType: BlockType) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(NoticeListViewModel::class.java)) {
                NoticeListViewModel(blockType = blockType) as T
            } else {
                throw IllegalArgumentException("NoticeListViewModel must have parameter(blockType: BlockType)")
            }
        }
    }

    private val api: SupportApiContractor by lazy {
        SupportApiContractor(blockType = blockType)
    }

    private val _result = MutableLiveData<ViewModelResult<MutableList<NoticeEntity>>>()
    val result: LiveData<ViewModelResult<MutableList<NoticeEntity>>> = _result

    fun request(offset: Int = 0, limit: Int = 50) {
        _result.value = ViewModelResult.InProgress
        api.retrieveNoticeList(offset = offset, limit = limit) {
            when (it) {
                is ContractResult.Success -> _result.value = Contract.postComplete(it.contract)
                is ContractResult.Failure -> _result.value = Contract.postError(it)
            }
        }
    }
}