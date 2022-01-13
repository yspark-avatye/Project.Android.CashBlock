package com.avatye.cashblock.base.component.contract.business

import com.avatye.cashblock.R
import com.avatye.cashblock.base.component.support.CoreUtil
import com.avatye.cashblock.base.internal.preference.SettingPreferenceData


object PolicyContractor {
    fun notifyUseAAID() {
        if (SettingPreferenceData.needNotifyUseAAID) {
            SettingPreferenceData.update(needNotifyUseAAID = false)
            CoreUtil.showToast(R.string.acb_common_message_use_aaid)
        }
    }
}