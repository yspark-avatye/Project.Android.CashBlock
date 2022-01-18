package com.avatye.cashblock.base.presentation.viewmodel.notice

import androidx.lifecycle.*
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.api.SupportApiContractor
import com.avatye.cashblock.base.component.domain.entity.support.NoticeEntity
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult

internal class NoticeViewViewModel(val blockType: BlockType) : ViewModel() {

    companion object {
        fun create(blockType: BlockType, viewModelStoreOwner: ViewModelStoreOwner): NoticeViewViewModel {
            return ViewModelProvider(viewModelStoreOwner, Factory(blockType = blockType)).get(NoticeViewViewModel::class.java)
        }
    }

    class Factory(private val blockType: BlockType) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(NoticeViewViewModel::class.java)) {
                NoticeViewViewModel(blockType = blockType) as T
            } else {
                throw IllegalArgumentException("NoticeViewViewModel must have parameter(blockType: BlockType)")
            }
        }
    }

    private val api: SupportApiContractor by lazy {
        SupportApiContractor(blockType = blockType)
    }

    private val _result = MutableLiveData<ViewModelResult<NoticeEntity>>()
    val result: LiveData<ViewModelResult<NoticeEntity>> = _result

    fun request(noticeId: String) {
        _result.value = ViewModelResult.InProgress
        api.retrieveNoticeView(noticeId = noticeId) {
            when (it) {
                is ContractResult.Success -> _result.value = Contract.postComplete(it.contract)
                is ContractResult.Failure -> _result.value = Contract.postError(it)
            }
        }
    }
}