package com.avatye.cashblock.base.component.support

import android.app.Activity
import android.app.Application
import com.avatye.cashblock.BuildConfig
import com.avatye.cashblock.base.Core
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.business.AccountContractor
import com.avatye.cashblock.base.component.contract.business.SettingContractor
import com.zoyi.channel.plugin.android.ChannelIO
import com.zoyi.channel.plugin.android.open.config.BootConfig
import com.zoyi.channel.plugin.android.open.enumerate.BootStatus

object ChannelTalkUtil {

    internal fun init(application: Application) = ChannelIO.initialize(application)

    fun open(activity: Activity, blockType: BlockType, fallback: () -> Unit) {
        if (ChannelIO.isBooted()) {
            ChannelIO.showMessenger(activity)
        } else {
            connect(activity = activity, appId = Core.appId, blockType = blockType, fallback = fallback)
        }
    }

    private fun connect(activity: Activity, appId: String, blockType: BlockType, fallback: () -> Unit) {
        if (!ChannelIO.isBooted()) {
            val channelIOProfile = com.zoyi.channel.plugin.android.open.model.Profile.create().apply {
                setProperty("AppID", appId)
                setProperty("AppName", SettingContractor.appInfoSetting.appName)
                setProperty("AppVersionName", Core.appVersionName)
                setProperty("SdkBlockName", blockType.name)
                setProperty("SdkVersionName", BuildConfig.X_BUILD_SDK_VERSION_NAME)
            }

            val bootSetting: BootConfig? = BootConfig.create("48f56d3b-754b-41b2-9f0e-cd6d398a0cd1").apply {
                this.memberId = AccountContractor.sdkUserId
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