package com.avatye.cashblock.feature.roulette.presentation.viewmodel.ticket

import androidx.lifecycle.*
import com.avatye.cashblock.feature.roulette.RouletteConfig.logger
import com.avatye.cashblock.feature.roulette.component.livedata.TicketBalanceLiveData
import com.avatye.cashblock.feature.roulette.component.livedata.TouchTicketLiveData
import com.avatye.cashblock.feature.roulette.component.livedata.VideoTicketLiveData

internal class TicketViewModel(private val lifecycleOwner: LifecycleOwner) : ViewModel() {
    internal companion object {
        fun create(viewModelStoreOwner: ViewModelStoreOwner, lifecycleOwner: LifecycleOwner): TicketViewModel {
            return ViewModelProvider(viewModelStoreOwner, Factory(lifecycleOwner = lifecycleOwner)).get(TicketViewModel::class.java)
        }
    }

    // region factory
    internal class Factory(private val lifecycleOwner: LifecycleOwner) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(TicketViewModel::class.java)) {
                TicketViewModel(lifecycleOwner = lifecycleOwner) as T
            } else {
                throw IllegalArgumentException("NoticeListViewModel must have parameter(tagName: String, lifecycleOwner: LifecycleOwner)")
            }
        }
    }
    // endregion

    private val tagName = "TicketViewModel"

    // region { balance }
    private val _balance = MutableLiveData(TicketBalanceLiveData.balance)
    val balance: LiveData<Int> = _balance
    // endregion

    // region { touch ticket }
    private val _touchTicket = MutableLiveData(TouchTicketLiveData.receivableCount)
    val touchTicket: LiveData<Int> = _touchTicket
    val touchTicketLimit = TouchTicketLiveData.limitCount
    // endregion

    // region { video ticket }
    private val _videoTicket = MutableLiveData(VideoTicketLiveData.receivableCount)
    val videoTicket: LiveData<Int> = _videoTicket
    val videoTicketLimit = VideoTicketLiveData.limitCount
    // endregion

    init {
        TicketBalanceLiveData.observe(lifecycleOwner) {
            _balance.value = it
        }
        TouchTicketLiveData.observe(lifecycleOwner) {
            _touchTicket.value = it
        }
        VideoTicketLiveData.observe(lifecycleOwner) {
            _videoTicket.value = it
        }
    }

    fun synchronization(complete: () -> Unit = {}) {
        syncBalance { _, _ ->
            logger.i(viewName = tagName) { "synchronization -> syncBalance -> complete" }
            syncTouchTicket { _, _ ->
                logger.i(viewName = tagName) { "synchronization -> syncBalance -> syncTouchTicket -> complete" }
                syncVideoTicket { _, _ ->
                    complete()
                    logger.i(viewName = tagName) { "synchronization -> syncBalance -> syncTouchTicket -> syncVideoTicket -> complete" }
                }
            }
        }
    }

    fun syncBalance(complete: (success: Boolean, syncValue: Int) -> Unit) {
        TicketBalanceLiveData.synchronization { success, syncValue -> complete(success, syncValue) }
    }

    fun syncTouchTicket(complete: (success: Boolean, syncValue: Int) -> Unit) {
        TouchTicketLiveData.synchronization { success, syncValue -> complete(success, syncValue) }
    }

    fun syncVideoTicket(complete: (success: Boolean, syncValue: Int) -> Unit) {
        VideoTicketLiveData.synchronization { success, syncValue -> complete(success, syncValue) }
    }
}