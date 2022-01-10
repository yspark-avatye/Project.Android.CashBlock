package com.avatye.cashblock.base.component.widget.decor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

internal class VerticalDividerItemDecoration(
    private val context: Context,
    private val drawableResId: Int,
    private val offset: Int = 0,
    private val includeBottom: Boolean = false
) : RecyclerView.ItemDecoration() {

    private var divider: Drawable? = null
    private var dividerPadding: Rect = Rect()

    init {
        val attributes = context.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
        divider = when {
            drawableResId > 0 -> ContextCompat.getDrawable(context, drawableResId)
            else -> attributes.getDrawable(0)
        }
        divider?.getPadding(dividerPadding)
        attributes.recycle()
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left: Int = parent.paddingLeft
        val right: Int = parent.width - parent.paddingRight
        val childCount: Int = parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            val position: Int = parent.getChildAdapterPosition(child)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top: Int = child.bottom + params.bottomMargin
            val bottom: Int = top + (divider?.intrinsicHeight ?: 0)
            if (position != state.itemCount - 1 || includeBottom) {
                divider?.setBounds(
                    left + dividerPadding.left,
                    top + dividerPadding.top,
                    right - dividerPadding.right,
                    bottom + dividerPadding.bottom
                )
                divider?.draw(c)
            }
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = 0
        outRect.top = offset / 2
        outRect.right = 0
        outRect.bottom = offset / 2
    }
}