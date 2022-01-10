package com.avatye.cashblock.base.component.domain.model.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.component.contract.data.CoreDataContract
import com.avatye.cashblock.base.component.domain.entity.base.ActionType
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.widget.dialog.DialogLoadingView
import com.avatye.cashblock.base.component.widget.dialog.IDialogView
import com.avatye.cashblock.base.internal.controller.BlockEventController
import com.avatye.cashblock.base.internal.server.serve.ServeTask
import com.avatye.cashblock.base.library.LogHandler
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager


abstract class CoreBaseActivity : AppCompatActivity() {

    abstract val blockCode: BlockCode
    abstract val blockName: String

    protected val viewTag: String = this::class.java.simpleName
    protected open val exitTransitionType: ActivityTransitionType = ActivityTransitionType.NONE
    protected var glider: RequestManager? = null
    protected var loadingView: DialogLoadingView? = null
    protected var broadcastWatcher: BroadcastReceiver? = null

    // region { LeakHandler }
    protected val leakHandler = LeakHandler()

    protected class LeakHandler : Handler(Looper.getMainLooper())
    // endregion

    // region { dialog set }
    protected var dialogViewSet: MutableSet<IDialogView?>? = null

    fun putDialogView(dialog: IDialogView?) {
        dialogViewSet?.plus(dialog) ?: run {
            dialogViewSet = mutableSetOf(dialog)
        }
    }

    protected fun releaseDialogView() {
        dialogViewSet?.forEach { it?.dismiss() }
        dialogViewSet?.clear()
        dialogViewSet = null
    }

    val isShowingDialogView: Boolean
        get() {
            var isShowing = false
            dialogViewSet?.forEach {
                if (it?.isAppeared() == true) {
                    isShowing = true
                    return@forEach
                }
            }
            return isShowing
        }
    // endregion

    // region { lifecycle - onResumed }
    private var isResumed = false
    protected open fun onResumed() {
        //NONE
    }

    override fun onResume() {
        super.onResume()
        if (isResumed) {
            onResumed()
        } else {
            isResumed = true
        }
    }
    // endregion

    // region { lifecycle - onPaused }
    private var isPaused = false
    protected open fun onPaused() {
        // NONE
    }

    override fun onPause() {
        super.onPause()
        if (isPaused) {
            onPaused()
        } else {
            isPaused = true
        }
    }
    // endregion

    // region { lifecycle - onDestroy }
    override fun onDestroy() {
        super.onDestroy()
        try {
            loadingView?.dismiss()
            // dialog message view null
            releaseDialogView()
            // all api call cancel
            ServeTask.cancelToQueue(context = this, viewTag)
            // image loader
            glider = null
            // broadcast watcher
            broadcastWatcher?.let {
                unregisterReceiver(it)
            }
            // logging
            LogHandler.i(moduleName = blockName) {
                "${this::class.java.simpleName} -> onDestroy()"
            }
        } catch (e: Exception) {
            LogHandler.e(throwable = e, moduleName = blockName) {
                "${this::class.java.simpleName} -> onDestroy"
            }
        }
    }
    // endregion

    // region { lifecycle - onCreate }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glider = Glide.with(this)
        loadingView = DialogLoadingView.create(this)
        broadcastWatcher = BroadcastWatchReceiver()
        broadcastWatcher?.let {
            registerReceiver(it, BlockEventController.makeBlockEventWatcherFilter())
        }
    }

    fun setContentViewWith(view: View, logKey: String? = null, logParam: HashMap<String, Any>? = null) {
        // content view
        setContentView(view)
        // post event log
        logKey?.let {
            CoreDataContract(blockCode = blockCode).let { contract ->
                contract.postEventLog(eventKey = it, eventParam = logParam)
            }
        }
    }

    protected open fun receiveActionUnAuthorized() {
        val name = this::class.java.simpleName
        if (!name.contains("InspectionActivity")) {
            leakHandler.post { finish() }
        }
    }

    protected open fun receiveActionInspection() {
        val name = this::class.java.simpleName
        if (!name.contains("InspectionActivity")) {
            leakHandler.post { finish() }
        }
    }

    protected open fun receiveActionForbidden() {
        val name = this::class.java.simpleName
        if (!name.contains("InspectionActivity")) {
            leakHandler.post { finish() }
        }
    }

    protected open fun receiveActionTicketBox() {
        //NONE
    }

    protected open fun receiveActionTicketBalance() {
        //NONE
    }

    protected open fun receiveActionTouchTicketCondition() {
        //NONE
    }

    protected open fun receiveActionVideoTicketCondition() {
        //NONE
    }

    protected open fun receiveActionWinnerBoard() {
        //NONE
    }

    private inner class BroadcastWatchReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            LogHandler.i(moduleName = blockName) {
                "${this::class.java.simpleName} -> BroadcastWatchReceiver -> onReceive { action: ${intent?.action ?: "null"} }"
            }
            val actionName = intent?.action ?: return
            if (actionName.isNotEmpty()) {
                when (actionName) {
                    ActionType.UNAUTHORIZED.actionName -> receiveActionUnAuthorized()
                    ActionType.INSPECTION.actionName -> receiveActionInspection()
                    ActionType.FORBIDDEN.actionName -> receiveActionForbidden()
                    ActionType.TICKET_BOX_UPDATE.actionName -> receiveActionTicketBox()
                    ActionType.TICKET_BALANCE_UPDATE.actionName -> receiveActionTicketBalance()
                    ActionType.TOUCH_TICKET_CONDITION_UPDATE.actionName -> receiveActionTouchTicketCondition()
                    ActionType.VIDEO_TICKET_CONDITION_UPDATE.actionName -> receiveActionVideoTicketCondition()
                    ActionType.WINNER_BOARD_UPDATE.actionName -> receiveActionWinnerBoard()
                }
            }
        }
    }
}