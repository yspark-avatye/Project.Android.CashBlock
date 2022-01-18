package com.avatye.cashblock.feature.roulette.component.widget.button

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.avatye.cashblock.feature.roulette.CashBlockRoulette
import com.avatye.cashblock.feature.roulette.component.controller.EntryController
import com.avatye.cashblock.feature.roulette.databinding.AcbsrWidgetButtonRouletteBinding

class ButtonRoulette(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    interface ActionCallback {
        fun onClicked(): Boolean
    }

    var actionCallback: ActionCallback? = null
    private val vb = AcbsrWidgetButtonRouletteBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        setOnClickListener {
            actionCallback?.let {
                if (it.onClicked()) {
                    startAction()
                }
            } ?: run { startAction() }
        }
    }

    private fun startAction() = EntryController.open(context = context)


    fun onResume() {

    }

    fun onDestroy() {

    }
}