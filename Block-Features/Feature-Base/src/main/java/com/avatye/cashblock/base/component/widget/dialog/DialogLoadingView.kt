package com.avatye.cashblock.base.component.widget.dialog

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialog
import com.avatye.cashblock.R

class DialogLoadingView private constructor(private val activity: Activity) : IDialogView {

    companion object {
        fun create(ownerActivity: Activity) = DialogLoadingView(ownerActivity)
    }

    private val dialog: AppCompatDialog by lazy {
        AppCompatDialog(activity).apply {
            this.setContentView(R.layout.acb_common_widget_dialog_loading)
            this.setOwnerActivity(activity)
            this.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun show(cancelable: Boolean) {
        if (activity.isFinishing) {
            return
        }
        this.dialog.setCanceledOnTouchOutside(cancelable)
        this.dialog.show()
    }

    override fun dismiss() {
        this.dialog.dismiss()
    }

    override fun isAppeared(): Boolean {
        return this.dialog.isShowing
    }
}