package com.avatye.cashblock.base.presentation.view.notice

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.avatye.cashblock.R
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.domain.model.sealed.ViewModelResult
import com.avatye.cashblock.base.component.support.CoreUtil
import com.avatye.cashblock.base.component.support.extraParcel
import com.avatye.cashblock.base.component.support.launch
import com.avatye.cashblock.base.component.widget.banner.BannerLinearView
import com.avatye.cashblock.base.presentation.AppBaseActivity
import com.avatye.cashblock.base.presentation.controller.ADController
import com.avatye.cashblock.base.presentation.parcel.NoticeParcel
import com.avatye.cashblock.base.presentation.viewmodel.notice.NoticeViewViewModel
import com.avatye.cashblock.databinding.AcbCommonActivityNoticeViewBinding

internal class NoticeViewActivity : AppBaseActivity() {

    companion object {
        fun open(activity: Activity, parcel: NoticeParcel, close: Boolean = false) {
            activity.launch(
                intent = Intent(activity, NoticeViewActivity::class.java).apply {
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

    private val vb: AcbCommonActivityNoticeViewBinding by lazy {
        AcbCommonActivityNoticeViewBinding.inflate(LayoutInflater.from(this))
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
        vb.bannerLinearView.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(view = vb.root, logKey = "view:core:notice-view")
        extraParcel<NoticeParcel>(NoticeParcel.NAME)?.let { parcel ->
            vb.headerView.viewType = parcel.headerType
            vb.headerView.actionClose { finish() }
            vb.headerView.actionBack { finish() }
            // view-model
            val viewModel = NoticeViewViewModel.create(
                blockType = parcel.blockType,
                viewModelStoreOwner = this@NoticeViewActivity
            )
            // view-model -> observe
            viewModel.result.observe(this) {
                when (it) {
                    is ViewModelResult.InProgress -> loadingView?.show(cancelable = false)
                    is ViewModelResult.Error -> {
                        loadingView?.dismiss()
                        CoreUtil.showToast(R.string.acb_common_message_error)
                        finish()
                    }
                    is ViewModelResult.Complete -> {
                        loadingView?.dismiss()
                        vb.noticeTitle.text = it.result.subject
                        vb.noticeDate.text = it.result.noticeDateTime?.toString("yyyy.MM.dd")
                        vb.noticeContent.loadData(
                            getString(R.string.acb_common_notice_detail_html, it.result.body),
                            "text/html; charset=utf-8",
                            "UTF-8"
                        )
                    }
                }
            }
            // banner
            vb.bannerLinearView.bannerData = ADController.createBannerData()
            vb.bannerLinearView.sourceType = when (getBlockType()) {
                BlockType.ROULETTE -> BannerLinearView.SourceType.ROULETTE
                BlockType.OFFERWALL -> BannerLinearView.SourceType.OFFERWALL
                else -> null
            }
            vb.bannerLinearView.requestBanner()
            // request
            parcel.noticeId?.let { viewModel.request(noticeId = it) }
        } ?: run {
            CoreUtil.showToast(R.string.acb_common_message_error)
            finish()
        }
    }
}