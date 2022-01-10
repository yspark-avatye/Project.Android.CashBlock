package com.avatye.cashblock.feature.roulette.component.widget.dialog

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import com.avatye.cashblock.base.component.support.toHtml
import com.avatye.cashblock.base.component.widget.dialog.IDialogView
import com.avatye.cashblock.feature.roulette.R
import com.avatye.cashblock.feature.roulette.databinding.AcbsrWidgetDialogPopupNotificationInduceBinding
import com.avatye.cashblock.feature.roulette.presentation.view.notification.NotificationHelpActivity

internal class DialogPopupNotificationView private constructor(private val ownerActivity: Activity) : IDialogView {

    internal companion object {
        fun create(ownerActivity: Activity) = DialogPopupNotificationView(ownerActivity)
    }

    private var dialog: AlertDialog? = null
    private val binding: AcbsrWidgetDialogPopupNotificationInduceBinding by lazy {
        AcbsrWidgetDialogPopupNotificationInduceBinding.inflate(LayoutInflater.from(ownerActivity), null, false)
    }
    private val builder: AlertDialog.Builder by lazy {
        AlertDialog.Builder(ownerActivity, R.style.CashBlock_Widget_Dialog).setView(binding.root)
    }

    init {
        binding.subtitle.text = ownerActivity.getString(R.string.acbsr_string_notification_induce_dialog_sub_title).toHtml
        binding.description1.text = ownerActivity.getString(R.string.acbsr_string_notification_induce_dialog_description_1).toHtml
        with(binding.description2) {
            text = ownerActivity.getString(R.string.acbsr_string_notification_induce_dialog_description_2).toHtml
            setOnClickListener {
                NotificationHelpActivity.open(activity = ownerActivity, close = false)
            }
        }
    }

    fun setActionCallback(callback: () -> Unit) {
        binding.buttonAction.setOnClickListener {
            dismiss()
            callback()
        }
    }

    fun setCloseCallback(callback: () -> Unit) {
        binding.buttonClose.setOnClickListener {
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