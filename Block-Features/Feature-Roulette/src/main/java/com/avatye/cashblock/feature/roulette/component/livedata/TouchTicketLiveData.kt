package com.avatye.cashblock.feature.roulette.component.livedata

import androidx.lifecycle.MutableLiveData
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.api.TicketApiContractor
import com.avatye.cashblock.base.component.contract.business.EventContractor
import com.avatye.cashblock.base.component.contract.business.SettingContractor
import com.avatye.cashblock.base.component.domain.entity.ticket.TicketType
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.RouletteConfig.logger
import com.avatye.cashblock.feature.roulette.component.data.PreferenceData
import org.joda.time.DateTime

internal object TouchTicketLiveData : MutableLiveData<Int>() {
    private const val tagName = "TouchTicketLiveData"
    private const val FREQUENCY: Long = (10 * 60 * 1000)
    private val apiContract = TicketApiContractor(blockType = RouletteConfig.blockType)

    var receivableCount: Int = PreferenceData.Ticket.touchReceivableCount
        private set(value) {
            if (field != value) {
                field = value
                PreferenceData.Ticket.update(touchReceivableCount = value)
                TouchTicketLiveData.postValue(value)
                // send broadcast event
                EventContractor.postTouchTicketConditionUpdate(blockType = BlockType.ROULETTE)
            }
        }

    var limitCount: Int = SettingContractor.touchTicketSetting.limitCount
        private set(value) {
            if (field != value) {
                field = value
            }
        }

    fun synchronization(callback: (success: Boolean, syncValue: Int) -> Unit) {
        apiContract.retrieveCondition(ticketType = TicketType.TOUCH) {
            when (it) {
                is ContractResult.Success -> {
                    logger.i(viewName = tagName) { "synchronization -> TicketType.TOUCH { success: ${it.contract} }" }
                    limitCount = it.contract.limitCount
                    receivableCount = it.contract.receivableCount
                    callback(true, it.contract.receivableCount)
                }
                is ContractResult.Failure -> {
                    logger.i(viewName = tagName) { "synchronization -> TicketType.TOUCH { failure: $it }" }
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