package com.avatye.cashblock.base.component.widget.dialog

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import com.avatye.cashblock.R
import com.avatye.cashblock.databinding.AcbCommonWidgetDialogMessageBinding

class DialogMessageView private constructor(private val ownerActivity: Activity) : IDialogView {

    companion object {
        fun create(ownerActivity: Activity) = DialogMessageView(ownerActivity)
    }

    private var allowPositive = false
    private var allowNegative = false

    private val vb: AcbCommonWidgetDialogMessageBinding by lazy {
        AcbCommonWidgetDialogMessageBinding.inflate(LayoutInflater.from(ownerActivity), null, false)
    }

    private val dialog: AlertDialog by lazy {
        AlertDialog
            .Builder(ownerActivity, R.style.CashBlock_Widget_Dialog)
            .setView(vb.root)
            .create()
    }

    fun setTitle(title: CharSequence) = apply {
        vb.dialogTitle.text = title
        vb.dialogTitle.isVisible = true
    }

    fun setTitle(@StringRes resourceID: Int) = apply {
        vb.dialogTitle.setText(resourceID)
        vb.dialogTitle.isVisible = true
    }

    fun setMessage(message: CharSequence) = apply {
        vb.dialogMessage.text = message
        vb.dialogMessage.isVisible = true
    }

    fun setMessage(@StringRes resourceID: Int) = apply {
        vb.dialogMessage.setText(resourceID)
        vb.dialogMessage.isVisible = true
    }

    fun setPositiveButton(callback: () -> Unit = {}) = apply {
        allowPositive = true
        vb.dialogButtonPositive.isVisible = true
        vb.dialogButtonPositive.setOnClickListener {
            dialog.dismiss()
            callback()
        }
    }

    fun setPositiveButton(text: CharSequence, callback: () -> Unit = {}) = apply {
        allowPositive = true
        vb.dialogButtonPositive.text = text
        vb.dialogButtonPositive.isVisible = true
        vb.dialogButtonPositive.setOnClickListener {
            dialog.dismiss()
            callback()
        }
    }

    fun setPositiveButton(@StringRes resourceID: Int, callback: () -> Unit = {}) = apply {
        allowPositive = true
        vb.dialogButtonPositive.setText(resourceID)
        vb.dialogButtonPositive.isVisible = true
        vb.dialogButtonPositive.setOnClickListener {
            dialog.dismiss()
            callback()
        }
    }

    fun setNegativeButton(callback: () -> Unit = {}) = apply {
        allowNegative = true
        vb.dialogButtonNegative.isVisible = true
        vb.dialogButtonNegative.setOnClickListener {
            dialog.dismiss()
            callback()
        }
    }

    fun setNegativeButton(text: CharSequence, callback: () -> Unit = {}) = apply {
        allowNegative = true
        vb.dialogButtonNegative.text = text
        vb.dialogButtonNegative.isVisible = true
        vb.dialogButtonNegative.setOnClickListener {
            dialog.dismiss()
            callback()
        }
    }

    fun setNegativeButton(@StringRes resourceID: Int, callback: () -> Unit = {}) = apply {
        allowNegative = true
        vb.dialogButtonNegative.setText(resourceID)
        vb.dialogButtonNegative.isVisible = true
        vb.dialogButtonNegative.setOnClickListener {
            dialog.dismiss()
            callback()
        }
    }

    override fun show(cancelable: Boolean) {
        if (ownerActivity.isFinishing) {
            return
        }
        // divider
        vb.dialogButtonDivider.isVisible = allowPositive && allowNegative
        // prevent human error
        if (!allowPositive && !allowNegative) {
            vb.dialogButtonPositive.isVisible = true
            vb.dialogButtonPositive.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.setCancelable(cancelable)
        dialog.show()
    }

    override fun dismiss() {
        dialog.dismiss()
    }

    override fun isAppeared(): Boolean {
        return this.dialog.isShowing
    }
}