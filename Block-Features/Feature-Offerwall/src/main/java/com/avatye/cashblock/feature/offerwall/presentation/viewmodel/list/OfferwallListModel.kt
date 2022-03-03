package com.avatye.cashblock.feature.offerwall.presentation.viewmodel.list

import androidx.lifecycle.*
import com.avatye.cashblock.base.component.contract.api.OfferwallApiContractor
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallSectionEntity
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallTabEntity
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult
import com.avatye.cashblock.feature.offerwall.OfferwallConfig

internal class OfferwallListModel : ViewModel() {

    internal companion object {
        fun create(viewModelStoreOwner: ViewModelStoreOwner): OfferwallListModel {
            return ViewModelProvider(viewModelStoreOwner).get(OfferwallListModel::class.java)
        }
    }

    private val api: OfferwallApiContractor by lazy {
        OfferwallApiContractor(blockType = OfferwallConfig.blockType)
    }

    private val _tabResult = MutableLiveData<ViewModelResult<MutableList<OfferwallTabEntity>>>()
    val tabResult: LiveData<ViewModelResult<MutableList<OfferwallTabEntity>>> = _tabResult

    private val _listResult = MutableLiveData<ViewModelResult<MutableList<OfferwallSectionEntity>>>()
    val listResult: LiveData<ViewModelResult<MutableList<OfferwallSectionEntity>>> = _listResult


    fun requestTab() {
        _tabResult.value = Contract.postInProgress()
        api.retrieveTabs {
            when (it) {
                is ContractResult.Success -> _tabResult.value = Contract.postComplete(it.contract)
                is ContractResult.Failure -> _tabResult.value = Contract.postError(it)
            }
        }
    }


    fun requestList(
        deviceADID: String,
        service: ServiceType,
        tabID: String? = null
    ) {
        _listResult.value = Contract.postInProgress()
        api.retrieveList(deviceADID = deviceADID, tabID = tabID, service = service) {
            when (it) {
                is ContractResult.Success -> _listResult.value = Contract.postComplete(it.contract)
                is ContractResult.Failure -> _listResult.value = Contract.postError(it)
            }
        }
    }
}