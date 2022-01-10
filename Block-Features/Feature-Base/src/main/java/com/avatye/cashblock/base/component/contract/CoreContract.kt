package com.avatye.cashblock.base.component.contract

import android.content.Context
import com.avatye.cashblock.base.FeatureCore
import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.internal.preference.SettingPreferenceData
import com.avatye.cashblock.base.internal.server.serve.ServeEnvironment

object CoreContract {

    // region # core
    val coreContext: Context
        get() = FeatureCore.application

    val coreBlockCode: BlockCode
        get() = FeatureCore.coreBlockCode

    val isInitialized: Boolean
        get() = FeatureCore.isInitialized
    // endregion


    // region # config
    val appServiceName: String get() = FeatureCore.appServiceName

    val appVersionCode: String get() = FeatureCore.appVersionCode

    val appVersionName: String get() = FeatureCore.appVersionName

    val appName: String get() = FeatureCore.appName

    val appPackageName: String get() = FeatureCore.appPackageName
    // endregion


    // region # environment
    val allowLog: Boolean get() = FeatureCore.allowLog

    val allowDeveloper: Boolean get() = FeatureCore.allowDeveloper

    val appEnvironment: ServeEnvironment get() = FeatureCore.appEnvironment
    // endregion


    // region # custom data
    val appCustomData: String? get() = FeatureCore.appCustomData
    // endregion


    // region { Core Setting }
    object Setting {
        val needNotifyUseAAID: Boolean get() = SettingPreferenceData.needNotifyUseAAID
    }
    // endregion
}