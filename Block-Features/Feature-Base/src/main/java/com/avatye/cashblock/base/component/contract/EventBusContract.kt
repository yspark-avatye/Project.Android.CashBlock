package com.avatye.cashblock.base.component.contract

import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.internal.controller.BlockEventController

object EventBusContract {

    fun makeWatcherFilter() = BlockEventController.makeBlockEventWatcherFilter()

    fun postUnauthorized(blockType: BlockType) = BlockEventController.postEventUnauthorized(blockType = blockType)

    fun postForbidden(blockType: BlockType) = BlockEventController.postEventForbidden(blockType = blockType)

    fun postInspection(blockType: BlockType) = BlockEventController.postEventInspection(blockType = blockType)

    fun postTicketBalanceUpdate(blockType: BlockType) = BlockEventController.postEventTicketBalanceUpdate(blockType = blockType)

    fun postTicketBoxUpdate(blockType: BlockType) = BlockEventController.postEventTicketBoxUpdate(blockType = blockType)

    fun postTouchTicketConditionUpdate(blockType: BlockType) = BlockEventController.postEventTouchTicketConditionUpdate(blockType = blockType)

    fun postVideoTicketConditionUpdate(blockType: BlockType) = BlockEventController.postEventVideoTicketConditionUpdate(blockType = blockType)

    fun postBoxConditionUpdate(blockType: BlockType) = BlockEventController.postEventBoxConditionUpdate(blockType = blockType)

    fun postWinnerBoardUpdate(blockType: BlockType) = BlockEventController.postEventWinnerBoardUpdate(blockType = blockType)

    fun postAppLaunchMainActivity(blockType: BlockType) = BlockEventController.postEventAppLaunchMainActivity(blockType = blockType)

    fun postNotificationStatusUpdate(blockType: BlockType) = BlockEventController.postEventNotificationStatusUpdate(blockType = blockType)
}