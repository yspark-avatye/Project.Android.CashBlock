package com.avatye.cashblock.feature.roulette.component.livedata

import androidx.lifecycle.MutableLiveData
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.EventBusContract
import com.avatye.cashblock.base.component.contract.RemoteContract
import com.avatye.cashblock.base.component.contract.data.TicketDataContract
import com.avatye.cashblock.base.component.domain.entity.ticket.TicketType
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.feature.roulette.MODULE_NAME
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.component.data.PreferenceData
import org.joda.time.DateTime

// 사용처가 추가되면 Core 모듈로 이동 시키자
internal object VideoTicketLiveData : MutableLiveData<Int>() {
    private const val tagName = "VideoTicket@LiveData"
    private const val FREQUENCY: Long = (10 * 60 * 1000) //10min

    private val apiContract = TicketDataContract(blockCode = RouletteConfig.blockCode)

    var receivableCount: Int = PreferenceData.Ticket.videoReceivableCount
        private set(value) {
            if (field != value) {
                field = value
                PreferenceData.Ticket.update(videoReceivableCount = value)
                VideoTicketLiveData.postValue(value)
                // send broadcast event
                EventBusContract.postVideoTicketConditionUpdate(blockType = BlockType.ROULETTE)
            }
        }

    var limitCount: Int = RemoteContract.videoTicketSetting.limitCount
        private set(value) {
            if (field != value) {
                field = value
            }
        }

    fun synchronization(callback: (success: Boolean, syncValue: Int) -> Unit) {
        apiContract.retrieveCondition(ticketType = TicketType.VIDEO) {
            when (it) {
                is ContractResult.Success -> {
                    LogHandler.i(moduleName = MODULE_NAME) { "$tagName -> synchronization -> TicketType.VIDEO { success:${it.contract} }" }
                    limitCount = it.contract.limitCount
                    receivableCount = it.contract.receivableCount
                    callback(true, it.contract.receivableCount)
                }
                is ContractResult.Failure -> {
                    LogHandler.i(moduleName = MODULE_NAME) { "$tagName -> synchronization -> TicketType.VIDEO $it" }
                    receivableCount = -1
                    callback(false, -1)
                }
            }
        }
    }

    fun synchronizationFrequency(callback: (syncValue: Int) -> Unit) {
        val frequencyTime = DateTime().millis - PreferenceData.Ticket.videoReceivableSyncTime
        if (frequencyTime >= FREQUENCY) {
            synchronization { success, syncValue ->
                if (success) {
                    PreferenceData.Ticket.update(videoReceivableSyncTime = DateTime().millis)
                }
                callback(syncValue)
            }
        } else {
            callback(receivableCount)
        }
    }
}