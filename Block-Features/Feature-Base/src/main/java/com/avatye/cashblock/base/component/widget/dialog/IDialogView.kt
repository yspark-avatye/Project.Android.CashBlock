package com.avatye.cashblock.base.component.widget.dialog

interface IDialogView {
    fun show(cancelable: Boolean)
    fun dismiss()
    fun isAppeared(): Boolean
}