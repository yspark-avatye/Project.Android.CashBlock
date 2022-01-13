package com.avatye.cashblock.feature.roulette.component.livedata

import androidx.lifecycle.MutableLiveData
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.api.TicketApiContractor
import com.avatye.cashblock.base.component.contract.business.AccountContractor
import com.avatye.cashblock.base.component.contract.business.EventContractor
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.RouletteConfig.logger
import com.avatye.cashblock.feature.roulette.component.data.PreferenceData

// 사용처가 추가되면 Core 모듈로 이동 시키자
internal object TicketBalanceLiveData : MutableLiveData<Int>() {
    private const val tagName = "TicketBalanceLiveData"
    private val apiContract = TicketApiContractor(blockType = RouletteConfig.blockType)

    var balance: Int = PreferenceData.Ticket.balance
        private set(value) {
            if (field != value) {
                field = value
                PreferenceData.Ticket.update(balance = value)
                postValue(value)
                // send broadcast event
                EventContractor.postTicketBalanceUpdate(blockType = BlockType.ROULETTE)
            }
        }

    fun synchronization(balance: Int) {
        TicketBalanceLiveData.balance = balance
    }

    fun synchronization(callback: (success: Boolean, syncValue: Int) -> Unit) {
        if (!AccountContractor.isLogin) {
            callback(false, -1)
            return
        }
        apiContract.retrieveBalance {
            when (it) {
                is ContractResult.Success -> {
                    logger.i { "$tagName -> synchronization { success:${it.contract} }" }
                    balance = it.contract.balance
                    callback(true, it.contract.balance)
                }
                is ContractResult.Failure -> {
                    logger.i { "$tagName -> synchronization $it" }
                    balance = -1
                    callback(false, -1)
                }
            }
        }
    }
}