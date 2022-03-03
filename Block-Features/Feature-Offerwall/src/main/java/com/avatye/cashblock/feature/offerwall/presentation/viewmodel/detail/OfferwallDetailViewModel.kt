package com.avatye.cashblock.feature.offerwall.presentation.viewmodel.detail

import androidx.lifecycle.*
import com.avatye.cashblock.base.component.contract.api.OfferwallApiContractor
import com.avatye.cashblock.base.component.domain.entity.base.ServiceType
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallClickEntity
import com.avatye.cashblock.base.component.domain.entity.offerwall.OfferwallImpressionItemEntity
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult
import com.avatye.cashblock.feature.offerwall.OfferwallConfig

internal class OfferwallDetailViewModel : ViewModel() {

    internal companion object {
        fun create(viewModelStoreOwner: ViewModelStoreOwner): OfferwallDetailViewModel {
            return ViewModelProvider(viewModelStoreOwner).get(OfferwallDetailViewModel::class.java)
        }
    }

    private val api: OfferwallApiContractor by lazy {
        OfferwallApiContractor(blockType = OfferwallConfig.blockType)
    }

    private val _impressionResult = MutableLiveData<ViewModelResult<OfferwallImpressionItemEntity>>()
    val impressionResult: LiveData<ViewModelResult<OfferwallImpressionItemEntity>> = _impressionResult

    private val _clickResult = MutableLiveData<ViewModelResult<OfferwallClickEntity>>()
    val clickResult: LiveData<ViewModelResult<OfferwallClickEntity>> = _clickResult

    private val _conversionResult = MutableLiveData<ViewModelResult<Unit>>()
    val conversionResult: LiveData<ViewModelResult<Unit>> = _conversionResult

    private val _closeResult = MutableLiveData<ViewModelResult<Unit>>()
    val closeResult: LiveData<ViewModelResult<Unit>> = _closeResult


    fun requestImpression(deviceADID: String, advertiseID: String, service: ServiceType) {
        _impressionResult.value = Contract.postInProgress()
        api.postImpression(
            deviceADID = deviceADID,
            advertiseID = advertiseID,
            service = service,
        ) {
            when (it) {
                is ContractResult.Success -> _impressionResult.value = Contract.postComplete(it.contract)
                is ContractResult.Failure -> _impressionResult.value = Contract.postError(it)
            }
        }
    }


    fun requestConfirm(
        deviceADID: String,
        advertiseID: String,
        impressionID: String,
        service: ServiceType,
        deviceID: String? = null,
        deviceModel: String? = null,
        deviceNetwork: String? = null,
        deviceOS: String? = null,
        deviceCarrier: String? = null,
        customData: String? = null,
    ) {
        _clickResult.value = Contract.postInProgress()
        api.postClick(
            deviceADID = deviceADID,
            advertiseID = advertiseID,
            impressionID = impressionID,
            service = service,
            deviceID = deviceID,
            deviceModel = deviceModel,
            deviceNetwork = deviceNetwork,
            deviceOS = deviceOS,
            deviceCarrier = deviceCarrier,
            customData = customData
        ) {
            when (it) {
                is ContractResult.Success -> _clickResult.value = Contract.postComplete(it.contract)
                is ContractResult.Failure -> _clickResult.value = Contract.postError(it)
            }
        }
    }


    fun requestConversion(
        deviceADID: String,
        advertiseID: String,
        clickID: String,
        service: ServiceType,
        deviceID: String? = null,
        deviceNetwork: String? = null,
    ) {
        _conversionResult.value = Contract.postInProgress()
        api.postConversion(
            deviceADID = deviceADID,
            advertiseID = advertiseID,
            clickID = clickID,
            service = service,
            deviceID = deviceID,
            deviceNetwork = deviceNetwork,
        ) {
            when (it) {
                is ContractResult.Success -> _conversionResult.value = Contract.postComplete(it.contract)
                is ContractResult.Failure -> _conversionResult.value = Contract.postError(it)
            }
        }
    }


    fun requestClose(deviceADID: String, advertiseID: String) {
        _closeResult.value = Contract.postInProgress()
        api.postClose(deviceADID = deviceADID, advertiseID = advertiseID) {
            when (it) {
                is ContractResult.Success -> _closeResult.value = Contract.postComplete(it.contract)
                is ContractResult.Failure -> _closeResult.value = Contract.postError(it)
            }
        }
    }
}
