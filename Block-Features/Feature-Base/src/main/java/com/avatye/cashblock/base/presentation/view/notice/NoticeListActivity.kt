package com.avatye.cashblock.base.presentation.view.notice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avatye.cashblock.R
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.domain.entity.support.NoticeEntity
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult
import com.avatye.cashblock.base.component.support.CoreUtil
import com.avatye.cashblock.base.component.support.extraParcel
import com.avatye.cashblock.base.component.support.launch
import com.avatye.cashblock.base.component.support.setOnClickWithDebounce
import com.avatye.cashblock.base.component.widget.banner.BannerLinearView
import com.avatye.cashblock.base.component.widget.header.HeaderView
import com.avatye.cashblock.base.component.widget.miscellaneous.PlaceHolderRecyclerView
import com.avatye.cashblock.base.component.widget.miscellaneous.RecyclerViewScroller
import com.avatye.cashblock.base.presentation.AppBaseActivity
import com.avatye.cashblock.base.presentation.controller.AdvertiseController
import com.avatye.cashblock.base.presentation.parcel.NoticeParcel
import com.avatye.cashblock.base.presentation.viewmodel.notice.NoticeListViewModel
import com.avatye.cashblock.databinding.AcbCommonActivityNoticeListBinding
import com.avatye.cashblock.databinding.AcbCommonItemNoticeBinding

internal class NoticeListActivity : AppBaseActivity() {

    companion object {
        fun open(activity: Activity, parcel: NoticeParcel, close: Boolean = false) {
            activity.launch(
                intent = Intent(activity, NoticeListActivity::class.java).apply {
                    putExtra(NoticeParcel.NAME, parcel)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                },
                transition = ActivityTransitionType.NONE.value,
                close = close
            )
        }
    }

    override fun getBlockType(): BlockType {
        return extraParcel<NoticeParcel>(NoticeParcel.NAME)?.blockType ?: BlockType.CORE
    }

    private val vb: AcbCommonActivityNoticeListBinding by lazy {
        AcbCommonActivityNoticeListBinding.inflate(LayoutInflater.from(this))
    }

    override fun onResume() {
        super.onResume()
        vb.bannerLinearView.onResume()
    }

    override fun onPause() {
        super.onPause()
        vb.bannerLinearView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        vb.bannerLinearView.onDestroy()
    }

    private val limit: Int = 15
    private var offset: Int = 0
    private var hasNext: Boolean = false
    private val noticeAdapter: NoticeListAdapter = NoticeListAdapter()

    // view model
    private lateinit var parcel: NoticeParcel
    private lateinit var viewModel: NoticeListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(view = vb.root, logKey = "view:core:notice-list")
        val p = extraParcel<NoticeParcel>(NoticeParcel.NAME)
        if (p != null) {
            parcel = p
            viewModel = NoticeListViewModel(blockType = p.blockType)
        } else {
            CoreUtil.showToast(R.string.acb_common_message_error)
            finish()
            return
        }

        // header
        vb.headerView.actionBack { finish() }

        // region # list
        val linearLayoutManager = LinearLayoutManager(this)
        val scroller = object : RecyclerViewScroller(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                if (hasNext) {
                    hasNext = false
                    leakHandler.postDelayed({
                        viewModel.request(offset = offset, limit = limit)
                    }, 1000)
                }
            }
        }
        vb.placeHolderList.setHasFixedSize(hasFixedSize = true)
        vb.placeHolderList.setAddOnScrollListener(scroller)
        vb.placeHolderList.setLayoutManager(linearLayoutManager)
        vb.placeHolderList.setAdapter(noticeAdapter)
        vb.placeHolderList.status = PlaceHolderRecyclerView.Status.LOADING
        vb.placeHolderList.actionRetry {
            offset = 0
            hasNext = false
            viewModel.request(offset = offset, limit = limit)
        }
        // endregion

        // region # observe
        viewModel.result.observe(this) {
            when (it) {
                is ViewModelResult.InProgress -> {
                    if (offset == 0) {
                        loadingView?.show(cancelable = false)
                    }
                }
                is ViewModelResult.Complete -> {
                    viewBindNoticeList(noticeList = it.result)
                    loadingView?.dismiss()
                }
                is ViewModelResult.Error -> {
                    hasNext = false
                    noticeAdapter.updateHasNextItem(false)
                    if (offset == 0) {
                        vb.placeHolderList.status = PlaceHolderRecyclerView.Status.ERROR
                        CoreUtil.showToast(R.string.acb_common_message_error)
                    }
                    loadingView?.dismiss()
                }
            }
        }
        // endregion

        // banner
        vb.bannerLinearView.bannerData = AdvertiseController.createBannerData()
        vb.bannerLinearView.sourceType = when (getBlockType()) {
            BlockType.ROULETTE -> BannerLinearView.SourceType.ROULETTE
            BlockType.OFFERWALL -> BannerLinearView.SourceType.OFFERWALL
            else -> null
        }
        vb.bannerLinearView.requestBanner()

        // request
        viewModel.request(offset = offset, limit = limit)
    }

    private fun viewBindNoticeList(noticeList: MutableList<NoticeEntity>) {
        val isAppend = offset > 0
        if (!isAppend && noticeList.size == 0) {
            vb.placeHolderList.status = PlaceHolderRecyclerView.Status.EMPTY
            return
        }

        hasNext = noticeList.size >= limit
        offset += noticeList.size - 1
        noticeAdapter.updateData(noticeList, isAppend, hasNext)
        vb.placeHolderList.status = PlaceHolderRecyclerView.Status.CONTENT
    }

    private inner class NoticeListAdapter : RecyclerView.Adapter<NoticeListAdapter.ItemViewHolder>() {

        private var hasNextItem: Boolean = false
        private var noticeList: MutableList<NoticeEntity> = ArrayList()

        override fun getItemCount(): Int = noticeList.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val itemBinding = AcbCommonItemNoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ItemViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            holder.bindView(noticeList[position], position == itemCount - 1)
        }

        fun updateData(noticeList: MutableList<NoticeEntity>, isAppend: Boolean, hasNext: Boolean) {
            when (isAppend) {
                true -> this.noticeList.addAll(noticeList)
                false -> this.noticeList = noticeList
            }
            this.hasNextItem = hasNext
            this.notifyDataSetChanged()
        }

        fun updateHasNextItem(hasNext: Boolean) {
            this.hasNextItem = hasNext
            this.notifyDataSetChanged()
        }

        inner class ItemViewHolder(private val ib: AcbCommonItemNoticeBinding) : RecyclerView.ViewHolder(ib.root) {
            fun bindView(entity: NoticeEntity, isLastItem: Boolean) {
                itemView.setOnClickListener {
                    NoticeViewActivity.open(
                        activity = this@NoticeListActivity,
                        parcel = NoticeParcel(blockType = parcel.blockType, headerType = HeaderView.HeaderType.SUB, noticeId = entity.noticeID)
                    )
                }
                ib.listItemNoticeTitle.text = entity.subject
                ib.listItemNoticeDatetime.text = entity.noticeDateTime?.toString("yyyy.MM.dd")
                ib.listItemNoticeLoading.isVisible = isLastItem && hasNextItem
                ib.listItemNoticeEol.isVisible = isLastItem && !hasNextItem
                ib.listItemNoticeEol.setOnClickWithDebounce {
                    vb.placeHolderList.setSmoothScrollToPosition(0)
                }
            }
        }
    }
}