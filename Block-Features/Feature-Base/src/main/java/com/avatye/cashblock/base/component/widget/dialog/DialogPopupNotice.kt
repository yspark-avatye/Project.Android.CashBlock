package com.avatye.cashblock.base.component.widget.dialog

import android.app.Activity
import android.app.AlertDialog
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import com.avatye.cashblock.R
import com.avatye.cashblock.base.component.domain.entity.support.PopupDisplayType
import com.avatye.cashblock.base.component.domain.entity.support.PopupNoticeEntity
import com.avatye.cashblock.databinding.AcbCommonWidgetDialogPopupNoticeBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class DialogPopupNotice private constructor(
    private val ownerActivity: Activity,
    private val entity: PopupNoticeEntity,
    private val dialogAction: IDialogAction
) : IDialogView, View.OnClickListener {

    internal companion object {
        val NAME: String = DialogPopupNotice::class.java.simpleName
        fun create(ownerActivity: Activity, entity: PopupNoticeEntity, dialogAction: IDialogAction, callback: (dialog: DialogPopupNotice?) -> Unit) {
            Glide.with(ownerActivity.applicationContext).load(entity.imageUrl).listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    callback(null)
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    callback(DialogPopupNotice(ownerActivity = ownerActivity, entity = entity, dialogAction = dialogAction))
                    return false
                }
            }).preload()
        }
    }

    interface IDialogAction {
        fun onClose(popupID: String, displayType: PopupDisplayType? = null)
        fun onLanding(landingName: String, landingValue: String = "")
    }

    private val glider: RequestManager by lazy {
        Glide.with(ownerActivity.applicationContext)
    }

    private var dialog: AlertDialog? = null

    private val builder: AlertDialog.Builder by lazy {
        AlertDialog.Builder(ownerActivity, R.style.CashBlock_Widget_Dialog).setView(vb.root)
    }

    private val vb: AcbCommonWidgetDialogPopupNoticeBinding by lazy {
        AcbCommonWidgetDialogPopupNoticeBinding.inflate(LayoutInflater.from(ownerActivity), null, false)
    }

    init {
        when (entity.displayType) {
            PopupDisplayType.ONETIME -> {
                vb.popupNoticeButtonCloseOnce.isVisible = true
                vb.popupNoticeButtonCloseToday.isVisible = false
                vb.popupNoticeButtonCloseWeek.isVisible = false
                vb.popupNoticeButtonCloseNever.isVisible = false
                // close & divider
                vb.popupNoticeButtonClose.isVisible = false
                vb.popupNoticeCloseDivider.isVisible = false
                // event
                vb.popupNoticeButtonCloseOnce.setOnClickListener(this)
            }
            PopupDisplayType.TODAY -> {
                vb.popupNoticeButtonCloseOnce.isVisible = false
                vb.popupNoticeButtonCloseToday.isVisible = true
                vb.popupNoticeButtonCloseWeek.isVisible = false
                vb.popupNoticeButtonCloseNever.isVisible = false
                // close & divider
                vb.popupNoticeButtonClose.isVisible = true
                vb.popupNoticeCloseDivider.isVisible = true
                // event
                vb.popupNoticeButtonClose.setOnClickListener(this)
                vb.popupNoticeButtonCloseToday.setOnClickListener(this)
            }
            PopupDisplayType.WEEK -> {
                vb.popupNoticeButtonCloseOnce.isVisible = false
                vb.popupNoticeButtonCloseToday.isVisible = false
                vb.popupNoticeButtonCloseWeek.isVisible = true
                vb.popupNoticeButtonCloseNever.isVisible = false
                // close & divider
                vb.popupNoticeButtonClose.isVisible = true
                vb.popupNoticeCloseDivider.isVisible = true
                // event
                vb.popupNoticeButtonClose.setOnClickListener(this)
                vb.popupNoticeButtonCloseWeek.setOnClickListener(this)
            }
            PopupDisplayType.NEVER -> {
                vb.popupNoticeButtonCloseOnce.isVisible = false
                vb.popupNoticeButtonCloseToday.isVisible = false
                vb.popupNoticeButtonCloseWeek.isVisible = false
                vb.popupNoticeButtonCloseNever.isVisible = true
                // close & divider
                vb.popupNoticeButtonClose.isVisible = true
                vb.popupNoticeCloseDivider.isVisible = true
                // event
                vb.popupNoticeButtonClose.setOnClickListener(this)
                vb.popupNoticeButtonCloseNever.setOnClickListener(this)
            }
        }
        // landing
        if (entity.enableLanding) {
            vb.popupNoticeContent.setOnClickListener(this)
        }
        // image-load
        glider.load(entity.imageUrl).into(vb.popupNoticeContent)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // close- general
            vb.popupNoticeButtonClose.id -> {
                dismiss()
                dialogAction.onClose(popupID = entity.popupID)
            }
            // landing
            vb.popupNoticeContent.id -> {
                dismiss()
                dialogAction.onLanding(landingName = entity.landingName, landingValue = entity.landingValue)
            }
            // close - type
            vb.popupNoticeButtonCloseOnce.id,
            vb.popupNoticeButtonCloseToday.id,
            vb.popupNoticeButtonCloseWeek.id,
            vb.popupNoticeButtonCloseNever.id -> {
                dismiss()
                dialogAction.onClose(popupID = entity.popupID, displayType = entity.displayType)
            }
        }
    }

    override fun show(cancelable: Boolean) {
        if (ownerActivity.isFinishing) {
            return
        }
        dialog = builder.create()
        dialog?.setCancelable(cancelable)
        dialog?.show()
    }

    override fun dismiss() {
        dialog?.dismiss()
    }

    override fun isAppeared(): Boolean {
        return dialog?.isShowing ?: false
    }
}