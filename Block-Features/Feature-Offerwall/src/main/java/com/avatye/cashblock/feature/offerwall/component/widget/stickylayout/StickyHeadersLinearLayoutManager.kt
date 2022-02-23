package com.avatye.sdk.cashbutton.core.widget.stickylayout

import android.content.Context
import android.graphics.PointF
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.RecyclerView.Recycler
import java.util.*

internal open class StickyHeadersLinearLayoutManager<T>(context: Context?) : LinearLayoutManager(context) where T : RecyclerView.Adapter<*>, T : StickyHeaders? {
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var mTranslationX = 0f
    private var mTranslationY = 0f
    private val mHeaderPositions: ArrayList<Int> = arrayListOf()
    private val mHeaderPositionsObserver: AdapterDataObserver = HeaderPositionsAdapterDataObserver()
    private var mStickyHeader: View? = null
    private var mStickyHeaderPosition = -1
    private var mPendingScrollPosition = -1
    private var mPendingScrollOffset = 0

    fun setStickyHeaderTranslationY(translationY: Float) {
        mTranslationY = translationY
        requestLayout()
    }

    fun setStickyHeaderTranslationX(translationX: Float) {
        mTranslationX = translationX
        requestLayout()
    }

    fun isStickyHeader(view: View): Boolean {
        return view === mStickyHeader
    }

    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        setAdapter(view.adapter)
    }

    override fun onAdapterChanged(oldAdapter: RecyclerView.Adapter<*>?, newAdapter: RecyclerView.Adapter<*>?) {
        super.onAdapterChanged(oldAdapter, newAdapter)
        setAdapter(newAdapter)
    }

    private fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        mAdapter?.unregisterAdapterDataObserver(mHeaderPositionsObserver)
        if (adapter is StickyHeaders) {
            mAdapter = adapter
            mAdapter?.registerAdapterDataObserver(mHeaderPositionsObserver)
            mHeaderPositionsObserver.onChanged()
        } else {
            mAdapter = null
            mHeaderPositions.clear()
        }
    }

    override fun scrollVerticallyBy(dy: Int, recycler: Recycler, state: RecyclerView.State): Int {
        detachStickyHeader()
        val scrolled = super.scrollVerticallyBy(dy, recycler, state)
        attachStickyHeader()
        if (scrolled != 0) {
            updateStickyHeader(recycler)
        }
        return scrolled
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: Recycler, state: RecyclerView.State): Int {
        detachStickyHeader()
        val scrolled = super.scrollHorizontallyBy(dx, recycler, state)
        attachStickyHeader()
        if (scrolled != 0) {
            updateStickyHeader(recycler)
        }
        return scrolled
    }

    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        detachStickyHeader()
        super.onLayoutChildren(recycler, state)
        attachStickyHeader()
        if (!state.isPreLayout) {
            updateStickyHeader(recycler)
        }
    }

    override fun scrollToPosition(position: Int) = this.scrollToPositionWithOffset(position, -2147483648)

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        setPendingScroll(-1, -2147483648)
        val headerIndex = findHeaderIndexOrBefore(position)
        if (headerIndex != -1 && findHeaderIndex(position) == -1) {
            when {
                findHeaderIndex(position - 1) != -1 -> {
                    super.scrollToPositionWithOffset(position - 1, offset)
                }
                headerIndex == findHeaderIndex(mStickyHeaderPosition) -> {
                    mStickyHeader?.let {
                        val adjustedOffset = (if (offset != -2147483648) offset else 0) + it.height
                        super.scrollToPositionWithOffset(position, adjustedOffset)
                    }
                }
                else -> {
                    setPendingScroll(position, offset)
                    super.scrollToPositionWithOffset(position, offset)
                }
            }
        } else {
            super.scrollToPositionWithOffset(position, offset)
        }
    }

    override fun computeVerticalScrollExtent(state: RecyclerView.State): Int {
        detachStickyHeader()
        val extent = super.computeVerticalScrollExtent(state)
        attachStickyHeader()
        return extent
    }

    override fun computeVerticalScrollOffset(state: RecyclerView.State): Int {
        detachStickyHeader()
        val offset = super.computeVerticalScrollOffset(state)
        attachStickyHeader()
        return offset
    }

    override fun computeVerticalScrollRange(state: RecyclerView.State): Int {
        detachStickyHeader()
        val range = super.computeVerticalScrollRange(state)
        attachStickyHeader()
        return range
    }

    override fun computeHorizontalScrollExtent(state: RecyclerView.State): Int {
        detachStickyHeader()
        val extent = super.computeHorizontalScrollExtent(state)
        attachStickyHeader()
        return extent
    }

    override fun computeHorizontalScrollOffset(state: RecyclerView.State): Int {
        detachStickyHeader()
        val offset = super.computeHorizontalScrollOffset(state)
        attachStickyHeader()
        return offset
    }

    override fun computeHorizontalScrollRange(state: RecyclerView.State): Int {
        detachStickyHeader()
        val range = super.computeHorizontalScrollRange(state)
        attachStickyHeader()
        return range
    }

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        detachStickyHeader()
        val vector = super.computeScrollVectorForPosition(targetPosition)
        attachStickyHeader()
        return vector
    }

    override fun onFocusSearchFailed(focused: View, focusDirection: Int, recycler: Recycler, state: RecyclerView.State): View? {
        detachStickyHeader()
        val view = super.onFocusSearchFailed(focused, focusDirection, recycler, state)
        attachStickyHeader()
        return view
    }

    private fun detachStickyHeader() {
        mStickyHeader?.let { detachView(it) }
    }

    private fun attachStickyHeader() {
        mStickyHeader?.let { this.attachView(it) }
    }

    private fun updateStickyHeader(recycler: Recycler) {
        val headerCount = mHeaderPositions.size
        val childCount = this.childCount
        if (headerCount > 0 && childCount > 0) {
            var anchorView: View? = null
            var anchorIndex = -1
            var anchorPos = -1
            var headerIndex: Int
            headerIndex = 0
            while (headerIndex < childCount) {
                val child = getChildAt(headerIndex)
                val params = child?.layoutParams as RecyclerView.LayoutParams
                if (isViewValidAnchor(child, params) == true) {
                    anchorView = child
                    anchorIndex = headerIndex
                    anchorPos = params.viewAdapterPosition
                    break
                }
                ++headerIndex
            }
            if (anchorView != null && anchorPos != -1) {
                headerIndex = findHeaderIndexOrBefore(anchorPos)
                val headerPos = if (headerIndex != -1) mHeaderPositions[headerIndex] else -1
                val nextHeaderPos = if (headerCount > headerIndex + 1) mHeaderPositions[headerIndex + 1] else -1
                if (headerPos != -1) {
                    if(mStickyHeader?.tag != headerPos) {
                        mStickyHeader?.let {
                            if(getItemViewType(it) != mAdapter?.getItemViewType(headerPos)) {
                                scrapStickyHeader()
                            }
                        }

                        scrapStickyHeader()
                        createStickyHeader(recycler, headerPos)
                    }

                    var nextHeaderView: View? = null
                    if (nextHeaderPos != -1) {
                        nextHeaderView = getChildAt(anchorIndex + (nextHeaderPos - anchorPos))
                        if (nextHeaderView === mStickyHeader) {
                            nextHeaderView = null
                        }
                    }
                    mStickyHeader?.translationX = getX(mStickyHeader, nextHeaderView)?: 0F
                    mStickyHeader?.translationY = getY(mStickyHeader, nextHeaderView)?: 0F
                    return
                }
            }
        }
    }

    private fun createStickyHeader(recycler: Recycler, position: Int) {
        val stickyHeader = recycler.getViewForPosition(position)
        if (mAdapter is StickyHeaders.ViewSetup) {
            (mAdapter as StickyHeaders.ViewSetup).setupStickyHeaderView(stickyHeader)
        }
        stickyHeader.tag = position
        this.addView(stickyHeader)
        measureAndLayout(stickyHeader)
        ignoreView(stickyHeader)

        mStickyHeader = stickyHeader
        mStickyHeaderPosition = position
    }

    private fun measureAndLayout(stickyHeader: View?) {
        stickyHeader?.let {
            measureChildWithMargins(it, 0, 0)
            if (this.orientation == 1) {
                it.layout(
                        this.paddingLeft,
                        0,
                        this.width - this.paddingRight,
                        it.measuredHeight
                )
            } else {
                it.layout(0, this.paddingTop, it.measuredWidth, this.height - this.paddingBottom)
            }
        }
    }

    private fun scrapStickyHeader() {
        val stickyHeader = mStickyHeader
        stickyHeader?.let {
            mStickyHeader = null
            mStickyHeaderPosition = -1
            it.translationX = 0.0f
            it.translationY = 0.0f
            if (mAdapter is StickyHeaders.ViewSetup) {
                (mAdapter as StickyHeaders.ViewSetup).teardownStickyHeaderView(it)
            }

            this.stopIgnoringView(it)
            this.removeView(it)
        }
    }

    private fun isViewValidAnchor(view: View?, params: RecyclerView.LayoutParams): Boolean? {
        return if (!params.isItemRemoved && !params.isViewInvalid) {
            view?.let {
                if (this.orientation == 1) {
                    if (this.reverseLayout) {
                        it.top.toFloat() + view.translationY <= this.height.toFloat() + mTranslationY
                    } else {
                        it.bottom.toFloat() - view.translationY >= mTranslationY
                    }
                } else if (this.reverseLayout) {
                    it.left.toFloat() + view.translationX <= this.width.toFloat() + mTranslationX
                } else {
                    it.right.toFloat() - view.translationX >= mTranslationX
                }
            }
        } else {
            false
        }
    }

    private fun getY(headerView: View?, nextHeaderView: View?): Float? {
        return if (this.orientation == 1) {
            var y = mTranslationY
            headerView?.let {
                if (this.reverseLayout) {
                    y += (this.height - it.height).toFloat()
                }
                if (nextHeaderView != null) {
                    y = if (this.reverseLayout) {
                        nextHeaderView.bottom.toFloat().coerceAtLeast(y)
                    } else {
                        (nextHeaderView.top - it.height).toFloat().coerceAtMost(y)
                    }
                }
                y
            }
        } else {
            mTranslationY
        }
    }

    private fun getX(headerView: View?, nextHeaderView: View?): Float? {
        return if (this.orientation != 1) {
            var x = mTranslationX
            headerView?.let {
                if (this.reverseLayout) {
                    x += (this.width - it.width).toFloat()
                }
                if (nextHeaderView != null) {
                    x = if (this.reverseLayout) {
                        nextHeaderView.right.toFloat().coerceAtLeast(x)
                    } else {
                        (nextHeaderView.left - it.width).toFloat().coerceAtMost(x)
                    }
                }
                x
            }
        } else {
            mTranslationX
        }
    }

    private fun findHeaderIndex(position: Int): Int {
        var low = 0
        var high = mHeaderPositions.size - 1
        while (low <= high) {
            val middle = (low + high) / 2
            if (mHeaderPositions[middle] > position) {
                high = middle - 1
            } else {
                if (mHeaderPositions[middle] >= position) {
                    return middle
                }
                low = middle + 1
            }
        }
        return -1
    }

    private fun findHeaderIndexOrBefore(position: Int): Int {
        var low = 0
        var high = mHeaderPositions.size - 1
        while (true) {
            while (low <= high) {
                val middle = (low + high) / 2
                if (mHeaderPositions[middle] <= position) {
                    if (middle >= mHeaderPositions.size - 1 || mHeaderPositions[middle + 1] > position) {
                        return middle
                    }
                    low = middle + 1
                } else {
                    high = middle - 1
                }
            }
            return -1
        }
    }

    private fun findHeaderIndexOrNext(position: Int): Int {
        var low = 0
        var high = mHeaderPositions.size - 1
        while (true) {
            while (low <= high) {
                val middle = (low + high) / 2
                if (middle > 0 && mHeaderPositions[middle - 1] >= position) {
                    high = middle - 1
                } else {
                    if (mHeaderPositions[middle] >= position) {
                        return middle
                    }
                    low = middle + 1
                }
            }
            return -1
        }
    }

    private fun setPendingScroll(position: Int, offset: Int) {
        this.mPendingScrollPosition = position
        mPendingScrollOffset = offset
    }

    private inner class HeaderPositionsAdapterDataObserver : AdapterDataObserver() {
        override fun onChanged() {
            mHeaderPositions.clear()
            val itemCount = mAdapter?.itemCount
            itemCount?.let {
                for (i in 0 until it) {
                    if ((mAdapter as StickyHeaders).isStickyHeader(i)) {
                        mHeaderPositions.add(i)
                    }
                }
            }
            if (mStickyHeader != null) {
                scrapStickyHeader()
            }
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            val headerCount = mHeaderPositions.size
            var i: Int
            if (headerCount > 0) {
                i = findHeaderIndexOrNext(positionStart)
                while (i != -1 && i < headerCount) {
                    mHeaderPositions[i] = mHeaderPositions[i] + itemCount
                    ++i
                }
            }
            i = positionStart
            while (i < positionStart + itemCount) {
                if ((mAdapter as StickyHeaders).isStickyHeader(i)) {
                    val headerIndex = findHeaderIndexOrNext(i)
                    if (headerIndex != -1) {
                        mHeaderPositions.add(headerIndex, i)
                    } else {
                        mHeaderPositions.add(i)
                    }
                }
                ++i
            }
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            var headerCount = mHeaderPositions.size
            if (headerCount > 0) {
                var i = positionStart + itemCount - 1
                while (i >= positionStart) {
                    val index = findHeaderIndex(i)
                    if (index != -1) {
                        mHeaderPositions.removeAt(index)
                        --headerCount
                    }
                    --i
                }
                if (mStickyHeader != null && !mHeaderPositions.contains(mStickyHeaderPosition)) {
                    scrapStickyHeader()
                }
                i = findHeaderIndexOrNext(positionStart + itemCount)
                while (i != -1 && i < headerCount) {
                    mHeaderPositions[i] = mHeaderPositions[i] - itemCount
                    ++i
                }
            }
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            val headerCount = mHeaderPositions.size
            if (headerCount > 0) {
                var i: Int
                var headerPos: Int
                if (fromPosition < toPosition) {
                    i = findHeaderIndexOrNext(fromPosition)
                    while (i != -1 && i < headerCount) {
                        headerPos = mHeaderPositions[i]
                        if (headerPos >= fromPosition && headerPos < fromPosition + itemCount) {
                            mHeaderPositions[i] = headerPos - (toPosition - fromPosition)
                            sortHeaderAtIndex(i)
                        } else {
                            if (headerPos < fromPosition + itemCount || headerPos > toPosition) {
                                break
                            }
                            mHeaderPositions[i] = headerPos - itemCount
                            sortHeaderAtIndex(i)
                        }
                        ++i
                    }
                } else {
                    i = findHeaderIndexOrNext(toPosition)
                    while (i != -1 && i < headerCount) {
                        headerPos = mHeaderPositions[i]
                        if (headerPos >= fromPosition && headerPos < fromPosition + itemCount) {
                            mHeaderPositions[i] = headerPos + (toPosition - fromPosition)
                            sortHeaderAtIndex(i)
                        } else {
                            if (headerPos < toPosition || headerPos > fromPosition) {
                                break
                            }
                            mHeaderPositions[i] = headerPos + itemCount
                            sortHeaderAtIndex(i)
                        }
                        ++i
                    }
                }
            }
        }

        private fun sortHeaderAtIndex(index: Int) {
            val headerPos = mHeaderPositions.removeAt(index)
            val headerIndex = findHeaderIndexOrNext(headerPos)
            if (headerIndex != -1) {
                mHeaderPositions.add(headerIndex, headerPos)
            } else {
                mHeaderPositions.add(headerPos)
            }
        }
    }
}