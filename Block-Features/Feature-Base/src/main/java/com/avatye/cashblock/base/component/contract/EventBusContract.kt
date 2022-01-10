package com.avatye.cashblock.base.component.contract

import com.avatye.cashblock.base.internal.controller.BlockEventController

object EventBusContract {

    fun makeWatcherFilter() = BlockEventController.makeBlockEventWatcherFilter()

    fun postUnauthorized() = BlockEventController.postEventUnauthorized()

    fun postForbidden() = BlockEventController.postEventForbidden()

    fun postInspection() = BlockEventController.postEventInspection()

    fun postTicketBalanceUpdate() = BlockEventController.postEventTicketBalanceUpdate()

    fun postTicketBoxUpdate() = BlockEventController.postEventTicketBoxUpdate()

    fun postTouchTicketConditionUpdate() = BlockEventController.postEventTouchTicketConditionUpdate()

    fun postVideoTicketConditionUpdate() = BlockEventController.postEventVideoTicketConditionUpdate()

    fun postBoxConditionUpdate() = BlockEventController.postEventBoxConditionUpdate()

    fun postWinnerBoardUpdate() = BlockEventController.postEventWinnerBoardUpdate()

    fun postAppLaunchMainActivity() = BlockEventController.postEventAppLaunchMainActivity()

    fun postNotificationStatusUpdate() = BlockEventController.postEventNotificationStatusUpdate()
}