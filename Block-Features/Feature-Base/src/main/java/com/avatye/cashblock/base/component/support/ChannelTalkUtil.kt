package com.avatye.cashblock.base.component.support

import android.app.Activity
import android.app.Application
import com.avatye.cashblock.BuildConfig
import com.avatye.cashblock.base.FeatureCore
import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.component.contract.AccountContract
import com.avatye.cashblock.base.component.contract.RemoteContract
import com.zoyi.channel.plugin.android.ChannelIO
import com.zoyi.channel.plugin.android.open.config.BootConfig
import com.zoyi.channel.plugin.android.open.enumerate.BootStatus

object ChannelTalkUtil {

    internal fun init(application: Application) = ChannelIO.initialize(application)

    fun open(activity: Activity, blockCode: BlockCode, blockName: String, fallback: () -> Unit) {
        if (ChannelIO.isBooted()) {
            ChannelIO.showMessenger(activity)
        } else {
            connect(activity = activity, appId = blockCode.blockId, blockName = blockName, fallback = fallback)
        }
    }

    private fun connect(activity: Activity, appId: String, blockName: String, fallback: () -> Unit) {
        if (!ChannelIO.isBooted()) {
            val channelIOProfile = com.zoyi.channel.plugin.android.open.model.Profile.create().apply {
                setProperty("AppID", appId)
                setProperty("AppName", RemoteContract.appInfoSetting.appName)
                setProperty("AppVersionName", FeatureCore.appVersionName)
                setProperty("SdkBlockName", blockName)
                setProperty("SdkVersionName", BuildConfig.X_BUILD_SDK_VERSION_NAME)
            }

            val bootSetting: BootConfig? = BootConfig.create("48f56d3b-754b-41b2-9f0e-cd6d398a0cd1").apply {
                this.memberId = AccountContract.sdkUserID
                setProfile(channelIOProfile)
            }

            ChannelIO.boot(bootSetting) { bootStatus, _ ->
                if (bootStatus == BootStatus.SUCCESS) {
                    ChannelIO.showMessenger(activity)
                } else {
                    fallback()
                }
            }
        }
    }

    private fun disconnect() = ChannelIO.shutdown()
}