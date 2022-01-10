package com.avatye.cashblock.base.internal.controller

import com.avatye.cashblock.R
import com.avatye.cashblock.base.internal.preference.SettingPreferenceData
import com.avatye.cashblock.base.component.support.CoreUtil

internal object CoreController {

    private const val tagName = "CoreManager"

    fun notifyUseAAID() {
        if (SettingPreferenceData.needNotifyUseAAID) {
            SettingPreferenceData.update(needNotifyUseAAID = false)
            CoreUtil.showToast(R.string.acb_common_message_use_aaid)
        }
    }
}