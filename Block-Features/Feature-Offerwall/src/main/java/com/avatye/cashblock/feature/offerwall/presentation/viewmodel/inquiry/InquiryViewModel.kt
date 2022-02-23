package com.avatye.cashblock.feature.offerwall.presentation.viewmodel.inquiry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.avatye.cashblock.base.component.contract.api.OfferwallApiContractor
import com.avatye.cashblock.base.component.domain.model.contract.Contract
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult
import com.avatye.cashblock.feature.offerwall.OfferwallConfig

internal class InquiryViewModel : ViewModel() {

    internal companion object {
        fun create(viewModelStoreOwner: ViewModelStoreOwner): InquiryViewModel {
            return ViewModelProvider(viewModelStoreOwner).get(InquiryViewModel::class.java)
        }
    }

    private val api: OfferwallApiContractor by lazy {
        OfferwallApiContractor(blockType = OfferwallConfig.blockType)
    }

    fun postInquiry(
        contactID: String? = null,
        advertiseID: String,
        title: String? = null,
        contents: String,
        state: Int? = null,
        deviceADID: String,
        phone: String? = null,
        userName: String? = null,
        callback: (result: ViewModelResult<Boolean>) -> Unit
    ) {
        callback(Contract.postInProgress())
        api.postContactReward(
            contactID = contactID,
            advertiseID = advertiseID,
            title = title,
            contents = contents,
            state = state,
            deviceADID = deviceADID,
            phone = phone,
            userName = userName
        ) {
            when (it) {
                is ContractResult.Success -> callback(Contract.postComplete(true))
                is ContractResult.Failure -> callback(Contract.postError(it))
            }
        }
    }

    fun requestInquiryInfo(advertiseID: String, callback: (result: ViewModelResult<String>) -> Unit) {
        callback(Contract.postInProgress())
        api.retrieveContactRewardInfo(advertiseID = advertiseID) {
            when (it) {
                is ContractResult.Success -> callback(Contract.postComplete(it.contract.contactRequireMsg))
                is ContractResult.Failure -> callback(Contract.postError(it))
            }
        }
    }

}