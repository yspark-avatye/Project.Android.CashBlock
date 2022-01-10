package com.avatye.cashblock.feature.offerwall.component.widget.button

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.avatye.cashblock.feature.offerwall.CashBlockOfferwall
import com.avatye.cashblock.feature.offerwall.databinding.AcbsoWidgetButtonOfferwallBinding

class ButtonOfferwall(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    interface ActionCallback {
        fun onClicked(): Boolean
    }

    var actionCallback: ActionCallback? = null
    private val vb = AcbsoWidgetButtonOfferwallBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        setOnClickListener {
            actionCallback?.let {
                if (it.onClicked()) {
                    startAction()
                }
            } ?: run {
                startAction()
            }
        }
    }

    private fun startAction() {
        CashBlockOfferwall.open(context = context)
    }

    fun onResume() {

    }

    fun onDestroy() {

    }

}