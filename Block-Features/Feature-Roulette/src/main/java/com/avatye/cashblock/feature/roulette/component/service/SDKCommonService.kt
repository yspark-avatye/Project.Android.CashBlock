package com.avatye.cashblock.feature.roulette.component.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.avatye.cashblock.base.component.contract.AccountContract
import com.avatye.cashblock.base.library.LogHandler
import com.avatye.cashblock.feature.roulette.MODULE_NAME
import com.avatye.cashblock.feature.roulette.component.controller.NotificationController

internal class SDKCommonService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        LogHandler.i(moduleName = MODULE_NAME) {
            "SDKCommonService -> onReceive"
        }
        context?.let {
            if (intent?.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                // notification
                if (AccountContract.isLogin) {
                    when (NotificationController.Host.useHostNotification) {
                        true -> {
                            NotificationController.Host.checkAndStartNotification(context = it, callback = { success ->
                                LogHandler.i(moduleName = MODULE_NAME) {
                                    "SDKCommonService { action:android.intent.action.BOOT_COMPLETED, HOST, notification:$success }"
                                }
                            })
                        }
                        false -> {
                            NotificationController.SDK.checkAndStartNotification(context = it, callback = { success ->
                                LogHandler.i(moduleName = MODULE_NAME) {
                                    "SDKCommonService { action:android.intent.action.BOOT_COMPLETED, SDK, notification:$success }"
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}