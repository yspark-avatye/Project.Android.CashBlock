package com.avatye.cashblock.base.presentation.view.inspection

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.avatye.cashblock.R
import com.avatye.cashblock.base.component.domain.entity.base.ActivityTransitionType
import com.avatye.cashblock.base.component.support.extraParcel
import com.avatye.cashblock.base.component.support.launch
import com.avatye.cashblock.base.presentation.AppBaseActivity
import com.avatye.cashblock.base.presentation.parcel.InspectionParcel
import com.avatye.cashblock.databinding.AcbCommonActivityInspectionBinding

internal class InspectionActivity : AppBaseActivity() {
    companion object {
        fun open(activity: Activity, parcel: InspectionParcel, close: Boolean = true) {
            activity.launch(
                intent = Intent(activity, InspectionActivity::class.java).apply {
                    putExtra(InspectionParcel.NAME, parcel)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                },
                transition = ActivityTransitionType.NONE.value,
                close = close
            )
        }
    }

    private val vb: AcbCommonActivityInspectionBinding by lazy {
        AcbCommonActivityInspectionBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWith(view = vb.root, logKey = "view:core:inspection-view")
        extraParcel<InspectionParcel>(InspectionParcel.NAME)?.let {
            // period
            val fromDateTime = it.startDateTime?.toString("M.d HH:mm") ?: ""
            val toDateTime = it.endDateTime?.toString("M.d HH:mm") ?: ""
            if (fromDateTime.isNotEmpty() && toDateTime.isNotEmpty()) {
                vb.inspectionPeriodWrapper.isVisible = true
                vb.inspectionPeriod.text = getString(R.string.acb_common_inspection_period).format(fromDateTime, toDateTime)
            }
            // message
            with(vb.inspectionMessage) {
                if (it.message.isNotEmpty()) {
                    text = it.message
                } else {
                    setText(R.string.acb_common_inspection_description)
                }
            }
            // link
            if (it.link.isNotEmpty()) {
                vb.inspectionActionLink.isVisible = true
                vb.inspectionActionLink.setOnClickListener { _ ->
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.link)))
                }
            } else {
                vb.inspectionActionLink.isVisible = false
            }
        } ?: run {
            vb.inspectionMessage.isVisible = false
            vb.inspectionActionLink.isVisible = false
            vb.inspectionPeriodWrapper.isVisible = false
        }
        // finish
        vb.inspectionActionClose.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        return
    }
}