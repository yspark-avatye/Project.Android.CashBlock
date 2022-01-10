package com.avatye.cashblock.feature.roulette.component.livedata

import androidx.lifecycle.MutableLiveData
import com.avatye.cashblock.base.component.contract.EventBusContract
import com.avatye.cashblock.base.component.contract.RemoteContract
import com.avatye.cashblock.base.component.contract.data.TicketDataContract
import com.avatye.cashblock.base.component.domain.entity.ticket.TicketType
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.MODULE_NAME
import com.avatye.cashblock.feature.roulette.component.data.PreferenceData
import org.joda.time.DateTime

internal object TouchTicketLiveData : MutableLiveData<Int>() {
    private const val tagName = "TouchTicketLiveData"
    private const val FREQUENCY: Long = (10 * 60 * 1000)
    private val apiContract = TicketDataContract(blockCode = RouletteConfig.blockCode)

    var receivableCount: Int = PreferenceData.Ticket.touchReceivableCount
        private set(value) {
            if (field != value) {
                field = value
                PreferenceData.Ticket.update(touchReceivableCount = value)
                TouchTicketLiveData.postValue(value)
                // send broadcast event
                EventBusContract.postTouchTicketConditionUpdate()
            }
        }

    var limitCount: Int = RemoteContract.touchTicketSetting.limitCount
        private set(value) {
            if (field != value) {
                field = value
            }
        }

    fun synchronization(callback: (success: Boolean, syncValue: Int) -> Unit) {
        apiContract.retrieveCondition(ticketType = TicketType.TOUCH) {
            when (it) {
                is ContractResult.Success -> {
                    LogHandler.i(moduleName = MODULE_NAME) { "$tagName -> synchronization -> TicketType.TOUCH { success:${it.contract} }" }
                    limitCount = it.contract.limitCount
                    receivableCount = it.contract.receivableCount
                    callback(true, it.contract.receivableCount)
                }
                is ContractResult.Failure -> {
                    LogHandler.i(moduleName = MODULE_NAME) { "$tagName -> synchronization -> TicketType.TOUCH $it" }
                    receivableCount = -1
                    callback(false, -1)
                }
            }
        }
    }

    fun synchronizationFrequency(callback: (syncValue: Int) -> Unit) {
        val frequencyTime = DateTime().millis - PreferenceData.Ticket.touchReceivableSyncTime
        if (frequencyTime >= FREQUENCY) {
            synchronization { success, syncValue ->
                if (success) {
                    PreferenceData.Ticket.update(touchReceivableSyncTime = DateTime().millis)
                }
                callback(syncValue)
            }
        } else {
            callback(receivableCount)
        }
    }
}