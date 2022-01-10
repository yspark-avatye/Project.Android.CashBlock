package com.avatye.cashblock.base.component.widget.header

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.use
import androidx.core.view.isVisible
import com.avatye.cashblock.R
import com.avatye.cashblock.databinding.AcbCommonWidgetHeaderBinding

class HeaderView(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    enum class HeaderType(val value: Int) {
        MAIN(0), SUB(1), POPUP(2);

        companion object {
            fun from(value: Int): HeaderType {
                return when (value) {
                    0 -> MAIN
                    1 -> SUB
                    2 -> POPUP
                    else -> MAIN
                }
            }
        }
    }

    var viewType = HeaderType.MAIN
        set(value) {
            if (field != value) {
                field = value
                when (field) {
                    HeaderType.MAIN -> {
                        vb.headerTitle.textSize = 20F
                        vb.headerBack.isVisible = false
                    }
                    HeaderType.SUB -> {
                        vb.headerTitle.textSize = 16F
                        vb.headerBack.isVisible = true
                    }
                    HeaderType.POPUP -> {
                        vb.headerTitle.textSize = 20F
                        vb.headerBack.isVisible = false
                        vb.headerClose.isVisible = true
                    }
                }
            }
        }

    private val vb: AcbCommonWidgetHeaderBinding by lazy {
        AcbCommonWidgetHeaderBinding.inflate(LayoutInflater.from(context), this, true)
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.HeaderView).use {
            viewType = HeaderType.from(it.getInt(R.styleable.HeaderView_acb_widget_view_type, 0))
            vb.headerTitle.text = it.getString(R.styleable.HeaderView_acb_widget_title) ?: ""
            vb.headerMore.isVisible = it.getBoolean(R.styleable.HeaderView_acb_widget_visible_more, false)
            vb.headerClose.isVisible = it.getBoolean(R.styleable.HeaderView_acb_widget_visible_close, false)
        }
    }


    // region { visibility }
    var moreVisible: Boolean
        get() {
            return vb.headerMore.isVisible
        }
        set(value) {
            if (vb.headerMore.isVisible != value) {
                vb.headerMore.isVisible = value
            }
        }

    var closeVisible: Boolean
        get() {
            return vb.headerClose.isVisible
        }
        set(value) {
            if (vb.headerClose.isVisible != value) {
                vb.headerClose.isVisible = value
            }
        }

    var backVisible: Boolean
        get() {
            return vb.headerBack.isVisible
        }
        set(value) {
            if (vb.headerBack.isVisible != value) {
                vb.headerBack.isVisible = value
            }
        }
    // endregion


    // region { action }
    fun actionBack(callback: () -> Unit) {
        vb.headerBack.setOnClickListener {
            callback()
        }
    }

    fun actionMore(callback: () -> Unit) {
        vb.headerMore.setOnClickListener {
            callback()
        }
    }

    fun actionClose(callback: () -> Unit) {
        vb.headerClose.setOnClickListener {
            callback()
        }
    }
    // endregion


    // region { title }
    fun updateTitleText(title: String) {
        vb.headerTitle.text = title
    }

    fun updateTitleText(@StringRes resourceId: Int = 0) {
        vb.headerTitle.setText(resourceId)
    }
    // endregion


    // region { option }
    fun updateOptionFirst(isVisible: Boolean, @DrawableRes resourceId: Int? = null, callback: () -> Unit = {}) {
        vb.headerOptionFirst.isVisible = isVisible
        vb.headerOptionFirst.setOnClickListener { callback() }
        resourceId?.let { vb.headerOptionFirst.setImageResource(it) }
    }

    fun updateOptionSecond(isVisible: Boolean, @DrawableRes resourceId: Int? = null, callback: () -> Unit = {}) {
        vb.headerOptionSecond.isVisible = isVisible
        vb.headerOptionSecond.setOnClickListener { callback() }
        resourceId?.let { vb.headerOptionSecond.setImageResource(it) }
    }
    // endregion
}