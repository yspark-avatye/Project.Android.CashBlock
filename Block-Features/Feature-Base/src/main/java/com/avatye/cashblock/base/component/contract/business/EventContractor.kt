package com.avatye.cashblock.base.component.contract.business

import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.avatye.cashblock.base.Core
import com.avatye.cashblock.base.Core.logger
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.base.ActionType
import com.avatye.cashblock.base.component.domain.model.parcel.EventBusParcel
import com.avatye.cashblock.base.internal.controller.LoginController
import com.avatye.cashblock.base.internal.server.serve.ServeTask
import java.util.*
import kotlin.collections.HashSet

object EventContractor {

    private const val tagName: String = "BizEventContractor"

    // event set
    private val eventSets = Collections.synchronizedSet(HashSet<Int>())

    // base event filter
    fun makeWatcherFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(ActionType.UNAUTHORIZED.actionName)
            addAction(ActionType.FORBIDDEN.actionName)
            addAction(ActionType.INSPECTION.actionName)
            addAction(ActionType.TICKET_BOX_UPDATE.actionName)
            addAction(ActionType.TICKET_BALANCE_UPDATE.actionName)
            addAction(ActionType.TOUCH_TICKET_CONDITION_UPDATE.actionName)
            addAction(ActionType.VIDEO_TICKET_CONDITION_UPDATE.actionName)
            addAction(ActionType.BOX_CONDITION_UPDATE.actionName)
            addAction(ActionType.NOTIFICATION_STATUS_UPDATE.actionName)
            addAction(ActionType.WINNER_BOARD_UPDATE.actionName)
        }
    }

    fun postUnauthorized(blockType: BlockType) {
        if (Core.isInitialized) {
            ServeTask.cancelToAllQueue(Core.application)
            requestBroadcast(blockType = blockType, actionType = ActionType.UNAUTHORIZED) {
                LoginController.requestLogout()
                logger.i(viewName = tagName) { "postUnauthorized -> requestBroadcast -> callback -> clearSession()" }
            }
        }
    }

    fun postForbidden(blockType: BlockType) {
        if (Core.isInitialized) {
            ServeTask.cancelToAllQueue(Core.application)
            requestBroadcast(blockType = blockType, actionType = ActionType.FORBIDDEN) {
                logger.i(viewName = tagName) { "postForbidden -> requestBroadcast" }
            }
        }
    }

    fun postInspection(blockType: BlockType) {
        if (Core.isInitialized) {
            ServeTask.cancelToAllQueue(Core.application)
            requestBroadcast(blockType = blockType, actionType = ActionType.INSPECTION) {
                logger.i(viewName = tagName) { "postInspection -> requestBroadcast" }
            }
        }
    }

    fun postTicketBalanceUpdate(blockType: BlockType) {
        requestBroadcast(blockType = blockType, actionType = ActionType.TICKET_BALANCE_UPDATE) {
            logger.i(viewName = tagName) { "postTicketBalanceUpdate -> requestBroadcast" }
        }
    }

    fun postTicketBoxUpdate(blockType: BlockType) {
        requestBroadcast(blockType = blockType, actionType = ActionType.TICKET_BOX_UPDATE) {
            logger.i(viewName = tagName) { "postTicketBoxUpdate -> requestBroadcast" }
        }
    }

    fun postTouchTicketConditionUpdate(blockType: BlockType) {
        requestBroadcast(blockType = blockType, actionType = ActionType.TOUCH_TICKET_CONDITION_UPDATE) {
            logger.i(viewName = tagName) { "postTouchTicketConditionUpdate -> requestBroadcast" }
        }
    }

    fun postVideoTicketConditionUpdate(blockType: BlockType) {
        requestBroadcast(blockType = blockType, actionType = ActionType.VIDEO_TICKET_CONDITION_UPDATE) {
            logger.i(viewName = tagName) { "postVideoTicketConditionUpdate -> requestBroadcast" }
        }
    }

    fun postBoxConditionUpdate(blockType: BlockType) {
        requestBroadcast(blockType = blockType, actionType = ActionType.BOX_CONDITION_UPDATE) {
            logger.i(viewName = tagName) { "postBoxConditionUpdate -> requestBroadcast" }
        }
    }

    fun postWinnerBoardUpdate(blockType: BlockType) {
        requestBroadcast(blockType = blockType, actionType = ActionType.WINNER_BOARD_UPDATE) {
            logger.i(viewName = tagName) { "postWinnerBoardUpdate -> requestBroadcast" }
        }
    }

    fun postNotificationStatusUpdate(blockType: BlockType) {
        requestBroadcast(blockType = blockType, actionType = ActionType.NOTIFICATION_STATUS_UPDATE) {
            logger.i(viewName = tagName) { "postNotificationStatusUpdate -> requestBroadcast" }
        }
    }

    fun postAppLaunchMainActivity(blockType: BlockType) {
        requestBroadcast(blockType = blockType, actionType = ActionType.APP_LAUNCH_MAIN) {
            logger.i(viewName = tagName) { "postAppLaunchMainActivity -> requestBroadcast" }
        }
    }

    private fun requestBroadcast(blockType: BlockType, actionType: ActionType, callback: () -> Unit) {
        if (!eventSets.contains(actionType.actionCode)) {
            logger.i(viewName = tagName) {
                "requestBroadcast -> sendMessageDelayed { delayMillis: 150, actionCode: ${actionType.actionCode}, actionName: ${actionType.actionName} }"
            }
            eventSets.add(actionType.actionCode)
            val message: Message = Message().apply {
                what = actionType.actionCode
                obj = actionType.actionName
                arg1 = blockType.value
            }
            onActionHandler.sendMessageDelayed(message, 150)
            callback()
            return
        }
        logger.i(viewName = tagName) {
            "requestBroadcast -> ignore { actionCode: ${actionType.actionCode}, actionName: ${actionType.actionName} }"
        }
    }

    private val onActionHandler = ActionHandler()

    private class ActionHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val actionCode = msg.what
            val actionName = msg.obj.toString()
            val blockType = BlockType.from(msg.arg1)
            Core.application.sendBroadcast(Intent().apply {
                setPackage(Core.appPackageName)
                action = actionName
                blockType?.let { putExtra(EventBusParcel.NAME, EventBusParcel(blockType = it)) }
            })
            if (eventSets.contains(actionCode)) {
                eventSets.remove(actionCode)
            }
            logger.i(viewName = tagName) { "ActionHandler -> handleMessage { actionCode: $actionCode, actionName: $actionName }" }
        }
    }


}