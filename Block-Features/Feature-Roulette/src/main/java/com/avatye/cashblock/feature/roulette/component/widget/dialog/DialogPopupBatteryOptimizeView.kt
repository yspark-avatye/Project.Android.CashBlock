package com.avatye.cashblock.feature.roulette.component.widget.dialog

import android.app.Activity
import android.app.Dialog
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import com.avatye.cashblock.base.component.contract.business.SettingContractor
import com.avatye.cashblock.base.component.contract.business.ViewOpenContractor
import com.avatye.cashblock.base.component.support.DeviceUtil
import com.avatye.cashblock.base.component.support.setOnClickWithDebounce
import com.avatye.cashblock.base.component.support.toHtml
import com.avatye.cashblock.base.component.widget.dialog.IDialogView
import com.avatye.cashblock.feature.roulette.R
import com.avatye.cashblock.feature.roulette.RouletteConfig
import com.avatye.cashblock.feature.roulette.component.data.PreferenceData
import com.avatye.cashblock.feature.roulette.databinding.AcbsrWidgetDialogBatteryOptimizeBinding
import org.joda.time.DateTime

internal class DialogPopupBatteryOptimizeView private constructor(
    private val activity: Activity
) : Dialog(activity, R.style.CashBlock_Widget_Dialog_Popup), IDialogView {

    internal companion object {
        fun create(ownerActivity: Activity) = DialogPopupBatteryOptimizeView(ownerActivity)
    }

    private val vb: AcbsrWidgetDialogBatteryOptimizeBinding by lazy {
        AcbsrWidgetDialogBatteryOptimizeBinding.inflate(LayoutInflater.from(activity), null, false)
    }

    init {
        setContentView(vb.root)
        window?.let {
            it.attributes = WindowManager.LayoutParams().apply {
                copyFrom(it.attributes)
                gravity = Gravity.BOTTOM
            }
        }
        vb.dialogBatteryOptimizeMessage.text = activity.getString(R.string.acbsr_string_battery_optimize_text)
            .format(SettingContractor.appInfoSetting.appName)
            .toHtml
        vb.dialogBatteryOptimizeDetail.setOnClickListener {
            ViewOpenContractor.openNoticeView(
                activity = activity,
                blockType = RouletteConfig.blockType,
                noticeID = "31bc94fe53c34f41be75e3d577e4abf5"
            )
        }
        vb.dialogBatteryOptimizeClose.setOnClickListener {
            dismiss()
        }
        vb.dialogBatteryOptimizeSetting.setOnClickWithDebounce {
            DeviceUtil.Battery.requestOptimization(context = activity)
            dismiss()
        }
    }

    override fun show(cancelable: Boolean) {
        if (activity.isFinishing) {
            return
        }
        setCancelable(cancelable)
        super.show()
    }

    override fun dismiss() {
        PreferenceData.Notification.update(
            batteryOptimizeDialogCheckDate = DateTime().plusDays(7).toString("yyyyMMdd").toInt()
        )
        super.dismiss()
    }

    override fun isAppeared(): Boolean {
        return super.isShowing()
    }
}