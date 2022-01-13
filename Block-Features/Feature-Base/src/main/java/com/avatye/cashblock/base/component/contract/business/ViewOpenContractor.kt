package com.avatye.cashblock.base.component.contract.business

import android.app.Activity
import com.avatye.cashblock.base.Core
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.support.takeIfNullOrEmpty
import com.avatye.cashblock.base.component.widget.header.HeaderView
import com.avatye.cashblock.base.presentation.parcel.InspectionParcel
import com.avatye.cashblock.base.presentation.parcel.NoticeParcel
import com.avatye.cashblock.base.presentation.parcel.TermsParcel
import com.avatye.cashblock.base.presentation.view.inspection.InspectionActivity
import com.avatye.cashblock.base.presentation.view.notice.NoticeListActivity
import com.avatye.cashblock.base.presentation.view.notice.NoticeViewActivity
import com.avatye.cashblock.base.presentation.view.terms.TermsViewActivity

object ViewOpenContractor {
    fun openNoticeList(
        activity: Activity,
        blockType: BlockType,
        headerType: HeaderView.HeaderType? = null
    ) {
        NoticeListActivity.open(
            activity = activity,
            parcel = NoticeParcel(
                blockType = blockType,
                headerType = headerType ?: HeaderView.HeaderType.POPUP
            ),
            close = false
        )
    }

    fun openNoticeView(
        activity: Activity,
        blockType: BlockType,
        headerType: HeaderView.HeaderType? = null,
        noticeID: String
    ) {
        NoticeViewActivity.open(
            activity = activity,
            parcel = NoticeParcel(
                blockType = blockType,
                headerType = headerType ?: HeaderView.HeaderType.POPUP,
                noticeId = noticeID
            ),
            close = false
        )
    }

    fun openTermsView(
        activity: Activity,
        blockType: BlockType,
        headerType: HeaderView.HeaderType? = null,
        url: String? = null,
        close: Boolean = false
    ) {
        TermsViewActivity.open(
            activity = activity,
            parcel = TermsParcel(
                blockType = blockType,
                headerType = headerType ?: HeaderView.HeaderType.POPUP,
                url = url.takeIfNullOrEmpty { "https://avatye.com/policy/terms/cashroulette?appID=${Core.appId}" }
            ),
            close = close
        )
    }

    fun openInspectionView(
        activity: Activity,
        blockType: BlockType,
        close: Boolean = true
    ) {
        // make parcel
        val parcel = InspectionParcel(
            blockType = blockType,
            startDateTime = Core.appInspection?.fromDateTime,
            endDateTime = Core.appInspection?.toDateTime,
            message = Core.appInspection?.message ?: "",
            link = Core.appInspection?.link ?: ""
        )
        // clear
        Core.appInspection = null
        // show activity
        InspectionActivity.open(activity = activity, parcel = parcel, close = close)
    }
}