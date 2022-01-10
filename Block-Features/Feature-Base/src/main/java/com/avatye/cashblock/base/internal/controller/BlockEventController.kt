package com.avatye.cashblock.base.internal.controller

import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.avatye.cashblock.base.FeatureCore
import com.avatye.cashblock.base.FeatureCore.logger
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.base.ActionType
import com.avatye.cashblock.base.component.domain.model.parcel.EventBusParcel
import com.avatye.cashblock.base.internal.server.serve.ServeTask
import java.util.*
import kotlin.collections.HashSet

internal object BlockEventController {

    private const val tagName = "BlockEventManager"

    private val eventSets = Collections.synchronizedSet(HashSet<Int>())

    fun makeBlockEventWatcherFilter(): IntentFilter {
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

    fun postEventUnauthorized(blockType: BlockType) {
        if (FeatureCore.isInitialized) {
            ServeTask.cancelToAllQueue(FeatureCore.application)
            requestBroadcast(
                blockType = blockType,
                actionType = ActionType.UNAUTHORIZED
            ) {
                LoginController.requestLogout()
                logger.i { "$tagName -> postEventUnauthorized -> requestBroadcast -> callback -> clearSession()" }
            }
        }
    }

    fun postEventForbidden(blockType: BlockType) {
        if (FeatureCore.isInitialized) {
            ServeTask.cancelToAllQueue(FeatureCore.application)
            requestBroadcast(
                blockType = blockType,
                actionType = ActionType.FORBIDDEN
            ) {
                logger.i { "$tagName -> postEventForbidden -> requestBroadcast" }
            }
        }
    }

    fun postEventInspection(blockType: BlockType) {
        if (FeatureCore.isInitialized) {
            ServeTask.cancelToAllQueue(FeatureCore.application)
            requestBroadcast(
                blockType = blockType,
                actionType = ActionType.INSPECTION
            ) {
                logger.i { "$tagName -> postEventInspection -> requestBroadcast" }
            }
        }
    }

    fun postEventTicketBalanceUpdate(blockType: BlockType) {
        requestBroadcast(
            blockType = blockType,
            actionType = ActionType.TICKET_BALANCE_UPDATE
        ) {
            logger.i { "$tagName -> postEventTicketBalanceUpdate -> requestBroadcast" }
        }
    }

    fun postEventTicketBoxUpdate(blockType: BlockType) {
        requestBroadcast(
            blockType = blockType,
            actionType = ActionType.TICKET_BOX_UPDATE
        ) {
            logger.i { "$tagName -> postEventTicketBoxUpdate -> requestBroadcast" }
        }
    }

    fun postEventTouchTicketConditionUpdate(blockType: BlockType) {
        requestBroadcast(
            blockType = blockType,
            actionType = ActionType.TOUCH_TICKET_CONDITION_UPDATE
        ) {
            logger.i { "$tagName -> postEventTouchTicketConditionUpdate -> requestBroadcast" }
        }
    }

    fun postEventVideoTicketConditionUpdate(blockType: BlockType) {
        requestBroadcast(
            blockType = blockType,
            actionType = ActionType.VIDEO_TICKET_CONDITION_UPDATE
        ) {
            logger.i { "$tagName -> postEventVideoTicketConditionUpdate -> requestBroadcast" }
        }
    }

    fun postEventBoxConditionUpdate(blockType: BlockType) {
        requestBroadcast(
            blockType = blockType,
            actionType = ActionType.BOX_CONDITION_UPDATE
        ) {
            logger.i { "$tagName -> postEventBoxConditionUpdate -> requestBroadcast" }
        }
    }

    fun postEventWinnerBoardUpdate(blockType: BlockType) {
        requestBroadcast(
            blockType = blockType,
            actionType = ActionType.WINNER_BOARD_UPDATE
        ) {
            logger.i { "$tagName -> postEventWinnerBoardUpdate -> requestBroadcast" }
        }
    }

    fun postEventNotificationStatusUpdate(blockType: BlockType) {
        requestBroadcast(
            blockType = blockType,
            actionType = ActionType.NOTIFICATION_STATUS_UPDATE
        ) {
            logger.i { "$tagName -> postEventNotificationStatusUpdate -> requestBroadcast" }
        }
    }

    fun postEventAppLaunchMainActivity(blockType: BlockType) {
        requestBroadcast(
            blockType = blockType,
            actionType = ActionType.APP_LAUNCH_MAIN
        ) {
            logger.i { "$tagName -> postEventAppLaunchMainActivity -> requestBroadcast" }
        }
    }

    private fun requestBroadcast(blockType: BlockType, actionType: ActionType, callback: () -> Unit) {
        if (!eventSets.contains(actionType.actionCode)) {
            logger.i {
                """
                   $tagName -> requestBroadcast -> sendMessageDelayed {
                        delayMillis: 150,
                        actionCode: ${actionType.actionCode},
                        actionName: ${actionType.actionName}
                    } 
                """.trimIndent()
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
        logger.i { "$tagName -> requestBroadcast -> ignore { actionCode: ${actionType.actionCode}, actionName: ${actionType.actionName} }" }
    }

    private val onActionHandler = ActionHandler()

    private class ActionHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val actionCode = msg.what
            val actionName = msg.obj.toString()
            val blockType = BlockType.from(msg.arg1)
            FeatureCore.application.sendBroadcast(Intent().apply {
                setPackage(FeatureCore.appPackageName)
                action = actionName
                blockType?.let {
                    putExtra(EventBusParcel.NAME, EventBusParcel(blockType = it))
                }
            })
            if (eventSets.contains(actionCode)) {
                eventSets.remove(actionCode)
            }
            logger.i { "$tagName -> ActionHandler -> handleMessage { actionCode: $actionCode, actionName: $actionName }" }
        }
    }
}