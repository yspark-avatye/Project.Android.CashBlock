package com.avatye.cashblock.feature.roulette.component.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.avatye.cashblock.base.component.contract.business.AccountContractor
import com.avatye.cashblock.feature.roulette.RouletteConfig.logger
import com.avatye.cashblock.feature.roulette.component.controller.NotificationController

internal class SDKCommonService : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        logger.i(viewName = "SDKCommonService") { "onReceive" }
        context?.let {
            if (intent?.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
                // notification
                if (AccountContractor.isLogin) {
                    when (NotificationController.Host.useHostNotification) {
                        true -> {
                            NotificationController.Host.checkAndStartNotification(context = it, callback = { success ->
                                logger.i(viewName = "SDKCommonService") {
                                    "action:android.intent.action.BOOT_COMPLETED, HOST, notification:$success "
                                }
                            })
                        }
                        false -> {
                            NotificationController.SDK.checkAndStartNotification(context = it, callback = { success ->
                                logger.i(viewName = "SDKCommonService") {
                                    "action:android.intent.action.BOOT_COMPLETED, SDK, notification:$success"
                                }
                            })
                        }
                    }
                }
            }
        }
    }
}