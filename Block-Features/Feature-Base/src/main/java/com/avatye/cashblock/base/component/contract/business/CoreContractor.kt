package com.avatye.cashblock.base.component.contract.business

import android.content.Context
import com.avatye.cashblock.base.Core
import com.avatye.cashblock.base.component.domain.entity.app.AppEnvironment

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
}