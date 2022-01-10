package com.avatye.cashblock.base.component.widget.miscellaneous

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.avatye.cashblock.base.MODULE_NAME
import com.avatye.cashblock.databinding.AcbCommonSkeletonItemLinearBinding
import com.avatye.cashblock.databinding.AcbCommonWidgetPlaceholderRecyclerviewBinding
import com.avatye.cashblock.base.library.LogHandler

class PlaceHolderRecyclerView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    enum class Status { LOADING, CONTENT, EMPTY, ERROR }

    private val vb: AcbCommonWidgetPlaceholderRecyclerviewBinding by lazy {
        AcbCommonWidgetPlaceholderRecyclerviewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    var status: Status = Status.LOADING
        set(value) {
            if (field != value) {
                field = value
                vb.frameLoading.isVisible = false
                vb.frameContent.isVisible = false
                vb.frameEmpty.isVisible = false
                vb.frameError.isVisible = false
                when (field) {
                    Status.LOADING -> vb.frameLoading.isVisible = true
                    Status.CONTENT -> vb.frameContent.isVisible = true
                    Status.EMPTY -> vb.frameEmpty.isVisible = true
                    Status.ERROR -> vb.frameError.isVisible = true
                }
            }
        }

    init {
        with(vb.frameLoading) {
            setHasFixedSize(true)
            adapter = PlaceHolderAdapter()
            vb.frameLoading.isVisible = true
        }
    }

    private val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            LogHandler.d(moduleName = MODULE_NAME) {
                "PlaceHolderRecyclerView -> AdapterDataObserver -> onChanged"
            }
            checkStatus()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            LogHandler.d(moduleName = MODULE_NAME) {
                "PlaceHolderRecyclerView -> AdapterDataObserver -> onItemRangeRemoved"
            }
            checkStatus()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            LogHandler.d(moduleName = MODULE_NAME) {
                "PlaceHolderRecyclerView -> AdapterDataObserver -> onItemRangeInserted"
            }
            checkStatus()
        }
    }

    private fun checkStatus() {
        vb.frameContent.adapter?.let {
            status = if (it.itemCount > 0) Status.CONTENT else Status.EMPTY
        } ?: run {
            status = Status.EMPTY
        }
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        try {
            vb.frameContent.adapter?.unregisterAdapterDataObserver(observer)
            vb.frameContent.adapter = adapter
            vb.frameContent.adapter?.registerAdapterDataObserver(observer)
            checkStatus()
        } catch (e: Exception) {
            LogHandler.e(throwable = e, moduleName = MODULE_NAME) {
                "ComplexListView -> setListAdapter"
            }
        }
    }

    fun setLayoutManager(layoutManager: RecyclerView.LayoutManager?) {
        vb.frameContent.layoutManager = layoutManager
    }

    fun setHasFixedSize(hasFixedSize: Boolean) {
        vb.frameContent.setHasFixedSize(hasFixedSize)
    }

    fun setAddOnScrollListener(onScrollListener: RecyclerView.OnScrollListener) {
        vb.frameContent.addOnScrollListener(onScrollListener)
    }

    fun setScrollToPosition(position: Int) {
        vb.frameContent.scrollToPosition(position)
    }

    fun setSmoothScrollToPosition(position: Int) {
        vb.frameContent.smoothScrollToPosition(position)
    }

    fun setAddItemDecoration(decor: RecyclerView.ItemDecoration) {
        vb.frameContent.addItemDecoration(decor)
    }

    fun getLayoutManager(): RecyclerView.LayoutManager? {
        return vb.frameContent.layoutManager
    }

    fun requestListPost(action: () -> Unit) {
        vb.frameContent.post {
            action()
        }
    }

    fun requestListPostDelayed(action: () -> Unit, delay: Long) {
        vb.frameContent.postDelayed({
            action()
        }, delay)
    }

    fun actionRetry(debounceTime: Long = 3000L, action: () -> Unit) {
        vb.retryButton.setOnClickListener {
            vb.retryButton.isEnabled = false
            Handler(Looper.getMainLooper()).postDelayed({
                vb.retryButton.isEnabled = true
            }, debounceTime)
            action()
        }
    }

    inner class PlaceHolderAdapter : RecyclerView.Adapter<PlaceHolderAdapter.ItemViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val viewHolder = AcbCommonSkeletonItemLinearBinding.inflate(LayoutInflater.from(parent.context))
            return ItemViewHolder(viewHolder)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        }

        override fun getItemCount(): Int = 20

        inner class ItemViewHolder(private val itemVB: AcbCommonSkeletonItemLinearBinding) : RecyclerView.ViewHolder(itemVB.root)
    }
}