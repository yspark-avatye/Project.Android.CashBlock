package com.avatye.cashblock.base.component.contract

import android.app.Activity
import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.component.support.takeIfNullOrEmpty
import com.avatye.cashblock.base.component.widget.header.HeaderView
import com.avatye.cashblock.base.presentation.parcel.NoticeParcel
import com.avatye.cashblock.base.presentation.parcel.TermsParcel
import com.avatye.cashblock.base.presentation.view.notice.NoticeListActivity
import com.avatye.cashblock.base.presentation.view.notice.NoticeViewActivity
import com.avatye.cashblock.base.presentation.view.terms.TermsViewActivity

object ViewOpenContract {
    fun openNoticeList(activity: Activity, blockCode: BlockCode, headerType: HeaderView.HeaderType? = null) {
        NoticeListActivity.open(
            activity = activity,
            parcel = NoticeParcel(
                blockId = blockCode.blockId,
                blockSecret = blockCode.blockSecret,
                blockType = blockCode.blockType,
                headerType = headerType ?: HeaderView.HeaderType.POPUP
            ),
            close = false
        )
    }

    fun openNoticeView(activity: Activity, blockCode: BlockCode, headerType: HeaderView.HeaderType? = null, noticeID: String) {
        NoticeViewActivity.open(
            activity = activity,
            parcel = NoticeParcel(
                blockId = blockCode.blockId,
                blockSecret = blockCode.blockSecret,
                blockType = blockCode.blockType,
                headerType = headerType ?: HeaderView.HeaderType.POPUP,
                noticeId = noticeID
            ),
            close = false
        )
    }

    fun openTermsView(
        activity: Activity,
        blockCode: BlockCode,
        headerType: HeaderView.HeaderType? = null,
        url: String? = null,
        close: Boolean = false
    ) {
        TermsViewActivity.open(
            activity = activity,
            parcel = TermsParcel(
                blockId = blockCode.blockId,
                blockSecret = blockCode.blockSecret,
                blockType = blockCode.blockType,
                headerType = headerType ?: HeaderView.HeaderType.POPUP,
                url = url.takeIfNullOrEmpty { "https://avatye.com/policy/terms/cashroulette?appID=${blockCode.blockId}" }
            ),
            close = close
        )
    }
}