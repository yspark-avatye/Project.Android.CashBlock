package com.avatye.cashblock.feature.roulette.component.widget.dialog

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.business.EventContractor
import com.avatye.cashblock.base.component.support.setOnClickWithDebounce
import com.avatye.cashblock.base.component.support.toHtml
import com.avatye.cashblock.base.component.widget.dialog.IDialogView
import com.avatye.cashblock.feature.roulette.R
import com.avatye.cashblock.feature.roulette.component.controller.NotificationController
import com.avatye.cashblock.feature.roulette.component.data.PreferenceData
import com.avatye.cashblock.feature.roulette.databinding.AcbsrWidgetDialogPopupTicketBoxInduceBinding
import com.avatye.cashblock.feature.roulette.presentation.AppBaseActivity
import com.avatye.cashblock.feature.roulette.presentation.view.notification.NotificationHelpActivity

internal class DialogPopupTicketBoxView private constructor(private val ownerActivity: Activity) : IDialogView {

    internal companion object {
        fun create(ownerActivity: Activity) = DialogPopupTicketBoxView(ownerActivity)
    }

    private var dialog: AlertDialog? = null
    private val vb: AcbsrWidgetDialogPopupTicketBoxInduceBinding by lazy {
        AcbsrWidgetDialogPopupTicketBoxInduceBinding.inflate(LayoutInflater.from(ownerActivity), null, false)
    }
    private val builder: AlertDialog.Builder by lazy {
        AlertDialog.Builder(ownerActivity, R.style.CashBlock_Widget_Dialog).setView(vb.root)
    }

    init {
        vb.subtitle1.text = ownerActivity.getString(R.string.acbsr_string_ticket_box_dialog_sub_title_1).toHtml
        vb.description1.text = ownerActivity.getString(R.string.acbsr_string_ticket_box_dialog_description_1).toHtml
        with(vb.description2) {
            text = ownerActivity.getString(R.string.acbsr_string_ticket_box_dialog_description_2).toHtml
            setOnClickListener {
                NotificationHelpActivity.open(activity = ownerActivity, close = false)
            }
        }
        // notification view
        updateNotificationView()
        // notification status
        if (NotificationController.Host.useHostNotification) {
            viewIntegrationNotificationStatus()
        } else {
            viewNotificationStatus()
        }
    }

    private fun viewNotificationStatus() {
        // click -> on
        vb.notificationStatusOn.setOnClickWithDebounce {
            if (NotificationController.SDK.allowNotification) {
                NotificationController.SDK.activationNotification(
                    activity = ownerActivity as AppBaseActivity,
                    callback = {
                        updateNotificationView()
                    }
                )
            }
        }
        // click -> off
        vb.notificationStatusOff.setOnClickWithDebounce {
            if (NotificationController.SDK.allowNotification) {
                NotificationController.SDK.deactivationNotification(activity = ownerActivity as AppBaseActivity)
                updateNotificationView()
            }
        }
    }

    private fun viewIntegrationNotificationStatus() {
        // click -> on
        vb.notificationStatusOn.setOnClickWithDebounce {
            EventContractor.postNotificationStatusUpdate(blockType = BlockType.ROULETTE)
            if (!NotificationController.SDK.allowNotification) {
                PreferenceData.Notification.update(allow = true)
                updateNotificationView()
                if (PreferenceData.Notification.allowHost) {
                    NotificationController.SDK.stopNotificationService(context = ownerActivity)
                } else {
                    NotificationController.SDK.startNotificationService(context = ownerActivity)
                }
            }
        }
        // click -> off
        vb.notificationStatusOff.setOnClickWithDebounce {
            EventContractor.postNotificationStatusUpdate(blockType = BlockType.ROULETTE)
            if (NotificationController.SDK.allowNotification) {
                PreferenceData.Notification.update(allow = false)
                NotificationController.SDK.stopNotificationService(context = ownerActivity)
                updateNotificationView()
            }
        }
    }

    private fun updateNotificationView() {
        if (NotificationController.SDK.allowNotification) {
            vb.notificationStatusOn.setBackgroundResource(R.drawable.acbsr_shape_switch_left_on)
            vb.notificationStatusOn.setTextColor(Color.WHITE)
            vb.notificationStatusOff.setBackgroundResource(R.drawable.acbsr_shape_switch_right_off)
            vb.notificationStatusOff.setTextColor(Color.BLACK)
        } else {
            vb.notificationStatusOn.setBackgroundResource(R.drawable.acbsr_shape_switch_left_off)
            vb.notificationStatusOn.setTextColor(Color.BLACK)
            vb.notificationStatusOff.setBackgroundResource(R.drawable.acbsr_shape_switch_right_on)
            vb.notificationStatusOff.setTextColor(Color.WHITE)
        }
    }

    fun setActionCallback(callback: () -> Unit) {
        vb.notificationContainer.isVisible = false
        vb.buttonAction.isVisible = true
        vb.buttonAction.setOnClickListener {
            dismiss()
            callback()
        }
    }

    fun setCloseCallback(callback: () -> Unit) {
        vb.buttonClose.setOnClickListener {
            dismiss()
            callback()
        }
    }

    override fun show(cancelable: Boolean) {
        if (ownerActivity.isFinishing) {
            return
        }
        dialog = builder.create()
        dialog?.setCancelable(cancelable)
        dialog?.show()
    }

    override fun dismiss() {
        dialog?.dismiss()
    }

    override fun isAppeared(): Boolean {
        return dialog?.isShowing ?: false
    }
}