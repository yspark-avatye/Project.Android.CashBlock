package com.avatye.cashblock.base.component.widget.miscellaneous

import android.content.Context
import android.util.AttributeSet
import kotlin.math.ceil

internal class FlexImageView : androidx.appcompat.widget.AppCompatImageView {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (drawable != null) {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = ceil((width * drawable.intrinsicHeight.toFloat() / drawable.intrinsicWidth).toDouble()).toInt()
            setMeasuredDimension(width, height)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}