package com.avatye.cashblock.feature.offerwall.presentation.viewmodel.inquiry

import androidx.lifecycle.*
import com.avatye.cashblock.base.component.contract.api.OfferwallApiContractor
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallContactRewardEntity
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult
import com.avatye.cashblock.feature.offerwall.OfferwallConfig

internal class InquiryListViewModel : ViewModel() {

    internal companion object {
        fun create(viewModelStoreOwner: ViewModelStoreOwner): InquiryListViewModel {
            return ViewModelProvider(viewModelStoreOwner).get(InquiryListViewModel::class.java)
        }
    }

    private val api: OfferwallApiContractor by lazy {
        OfferwallApiContractor(blockType = OfferwallConfig.blockType)
    }

    private val _result = MutableLiveData<ViewModelResult<MutableList<OfferwallContactRewardEntity>>>()
    val result: LiveData<ViewModelResult<MutableList<OfferwallContactRewardEntity>>> = _result

    fun request() {
        api.retrieveContactRewardList(blockType = OfferwallConfig.blockType) {
            when (it) {
                is ContractResult.Success -> _result.value = Contract.postComplete(it.contract)
                is ContractResult.Failure -> _result.value = Contract.postError(it)
            }
        }
    }

}