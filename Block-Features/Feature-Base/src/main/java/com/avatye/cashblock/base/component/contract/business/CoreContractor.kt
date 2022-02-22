package com.avatye.cashblock.base.component.contract.business

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import com.avatye.cashblock.R
import com.avatye.cashblock.base.Core
import com.avatye.cashblock.base.Core.logger
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.api.UserApiContractor
import com.avatye.cashblock.base.component.domain.entity.ad.AAIDEntity
import com.avatye.cashblock.base.component.domain.entity.app.AppEnvironment
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.component.support.CoreUtil
import com.avatye.cashblock.base.component.widget.dialog.DialogMessageView
import com.avatye.cashblock.base.internal.preference.SettingPreferenceData
import com.avatye.cashblock.base.library.ad.aaid.AdvertiseIDTask

object CoreContractor {
    // region # core
    val coreContext: Context
        get() = Core.application

    val isInitialized: Boolean
        get() = Core.isInitialized
    // endregion


    // region # config
    val appId: String
        get() {
            return if (Core.isInitialized) {
                Core.appId
            } else {
                ""
            }
        }

    val appVersionCode: String get() = Core.appVersionCode

    val appVersionName: String get() = Core.appVersionName

    val appName: String get() = Core.appName

    val appPackageName: String get() = Core.appPackageName
    // endregion


    // region # environment
    val allowLog: Boolean get() = Core.allowLog

    val allowDeveloper: Boolean get() = Core.allowDeveloper

    val appEnvironment: AppEnvironment get() = Core.appEnvironment
    // endregion


    // region # custom data
    val appCustomData: String? get() = Core.appCustomData
    // endregion


    // region # aaid
    object DeviceSetting {
        fun fetchAAID(callback: (success: Boolean) -> Unit) {
            retrieveAAID { result ->
                if (SettingPreferenceData.deviceAAID != result.aaid) {
                    updateAAID(result.aaid) { complete ->
                        if (complete && result.isValid) {
                            CoreUtil.showToast(R.string.acb_common_message_use_aaid)
                        }
                        // pass
                        callback(complete)
                    }
                } else {
                    // pass
                    callback(true)
                }
            }
        }

        fun retrieveAAID(callback: (result: AAIDEntity) -> Unit) {
            val currentAAID = SettingPreferenceData.deviceAAID
            AdvertiseIDTask(coreContext, callback = { isLimitAdTrackingEnabled, aaid ->
                callback(
                    AAIDEntity(
                        isLimitAdTrackingEnabled = isLimitAdTrackingEnabled,
                        aaid = aaid,
                        needUpdate = (currentAAID != aaid)
                    )
                )
            }).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR)
        }

        fun updateAAID(aaid: String, complete: (success: Boolean) -> Unit) {
            if (SettingPreferenceData.deviceAAID == aaid) {
                complete(true)
                return
            }
            // server
            UserApiContractor(blockType = BlockType.CORE).putUser(deviceADID = aaid) {
                when (it) {
                    is ContractResult.Success -> {
                        logger.i(viewName = "CoreContractor") { "updateAAID -> UserApiContractor -> Success" }
                        SettingPreferenceData.update(deviceAAID = aaid)
                        complete(true)
                    }
                    is ContractResult.Failure -> {
                        logger.i(viewName = "CoreContractor") { "updateAAID -> UserApiContractor -> Failure: $it" }
                        complete(false)
                    }
                }
            }
        }

        // dialog
        fun showLimitAdTrackingDialog(activity: Activity) {
            DialogMessageView.create(activity).apply {
                setMessage(resourceID = R.string.acb_common_message_limit_ad_tracking)
                setPositiveButton(resourceID = R.string.acb_common_button_setting_off) {
                    val action = "com.google.android.gms.settings.ADS_PRIVACY"
                    val settings = Intent(action)
                    activity.startActivity(settings)
                }
                setNegativeButton(resourceID = R.string.acb_common_button_cancel)
            }.show(cancelable = false)
        }

    }
    // endregion
}