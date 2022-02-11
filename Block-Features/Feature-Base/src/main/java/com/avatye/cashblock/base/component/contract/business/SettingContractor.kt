package com.avatye.cashblock.base.component.contract.business

import com.avatye.cashblock.BuildConfig
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.contract.api.CoreApiContractor
import com.avatye.cashblock.base.component.domain.entity.setting.*
import com.avatye.cashblock.base.component.domain.model.contract.ContractResult
import com.avatye.cashblock.base.internal.preference.RemotePreferenceData
import com.avatye.cashblock.base.library.miscellaneous.*
import org.joda.time.DateTime
import org.json.JSONObject

object SettingContractor {
    // application info setting
    val appInfoSetting
        get() = RemotePreferenceData.appInfo

    // application app setting
    val inAppSetting
        get() = RemotePreferenceData.inApp

    // advertise setting - ad network
    val advertiseNetworkSetting
        get() = RemotePreferenceData.advertiseNetwork

    // notification setting
    val notificationSetting
        get() = RemotePreferenceData.notification

    // mission setting
    val missionSetting
        get() = RemotePreferenceData.mission

    // touch ticket setting
    val touchTicketSetting
        get() = RemotePreferenceData.touchTicket

    // video ticket setting
    val videoTicketSetting
        get() = RemotePreferenceData.videoTicket

    // ticket vox setting
    val ticketBoxSetting
        get() = RemotePreferenceData.ticketBox


    internal object Controller {
        // is-synced
        val isSynced: Boolean
            get() {
                return RemotePreferenceData.version > 0L
            }

        //  sync-time
        private var syncTime: Long = 0L

        // fetch
        fun synchronization(blockType: BlockType, synchronized: (success: Boolean) -> Unit) {
            if (syncTime >= DateTime().millis) {
                synchronized(true)
            }
            // synchronization
            CoreApiContractor(blockType = blockType).let { core ->
                core.retrieveAppSettings { res ->
                    when (res) {
                        is ContractResult.Success -> {
                            // synctime
                            syncTime = DateTime().plusMinutes(1).millis
                            if (RemotePreferenceData.needUpdate(version = res.contract.updateDateTime)) {
                                try {
                                    res.contract.settings?.let { settings ->
                                        // block info settings
                                        settings.toJSONObjectValue("blockInfo")?.let {
                                            //syncAppConfig(it)
                                        }
                                        // app info settings
                                        settings.toJSONObjectValue("appInfo")?.let {
                                            syncAppConfig(config = it)
                                        }
                                        // notification settings
                                        settings.toJSONObjectValue("notification")?.let {
                                            syncNotificationConfig(config = it)
                                        }
                                        // advertise network settings
                                        settings.toJSONObjectValue("adNetwork")?.let {
                                            syncAdvertiseNetworkConfig(config = it)
                                        }
                                        // in-app settings
                                        settings.toJSONObjectValue("inApp")?.let {
                                            syncInAppConfig(config = it)
                                        }
                                        // p-id: touch ticket settings
                                        settings.toJSONObjectValue("touchTicket")?.let {
                                            syncTouchTicketConfig(config = it)
                                        }
                                        // p-id: video ticket settings
                                        settings.toJSONObjectValue("videoTicket")?.let {
                                            syncVideoTicketConfig(config = it)
                                        }
                                        // p-id: ticket box settings
                                        settings.toJSONObjectValue("ticketBox")?.let {
                                            syncTicketBoxConfig(config = it)
                                        }
                                        // mission settings
                                        settings.toJSONObjectValue("mission")?.let {
                                            syncMissionConfig(config = it)
                                        }
                                        // version
                                        syncVersion(res.contract.updateDateTime)
                                    }
                                } catch (e: Exception) {
                                    synchronized(false)
                                }
                            }
                            synchronized(true)
                        }
                        is ContractResult.Failure -> synchronized(false)
                    }
                }
            }
        }

        // sync version
        private fun syncVersion(version: Long) {
            RemotePreferenceData.updateVersion(remoteVersion = version, sdkVersion = BuildConfig.X_BUILD_SDK_VERSION_CODE)
        }

        // app block info
        private fun syncAppBlockConfig(config: JSONObject) {

        }

        // app setting
        private fun syncAppConfig(config: JSONObject) {
            val d = AppInfoSettingEntity.empty()
            val setting = AppInfoSettingEntity(
                appName = config.toStringValue("appName", d.appName),
                storeUrl = config.toStringValue("storeUrl", d.storeUrl),
                pointName = config.toStringValue("pointName", d.pointName),
                rouletteName = config.toStringValue("rouletteName", d.rouletteName),
                rouletteCampaign = config.toStringValue("rouletteCampaign", d.rouletteCampaign),
                tooltipMessage = config.toStringValue("tooltipMessage", d.tooltipMessage),
                allowLinearAD = config.toBooleanValue("allowLinearAD", d.allowLinearAD),
                allowMoreMenu = config.toBooleanValue("allowMoreMenu", d.allowMoreMenu),
                allowTicketBox = config.toBooleanValue("allowTicketBox", d.allowTicketBox),
                allowAgeVerification = config.toBooleanValue("useAgeVerification", d.allowAgeVerification)
            )
            RemotePreferenceData.fetchAppInfoSetting(setting = setting)
        }

        // notification setting
        private fun syncNotificationConfig(config: JSONObject) {
            val d = NotificationSettingEntity.empty()
            val setting = NotificationSettingEntity(
                inducePeriod = config.toLongValue("inducePeriod", d.inducePeriod),
                induceTicketCount = config.toIntValue("induceTicketCount", d.induceTicketCount)
            )
            RemotePreferenceData.fetchNotificationSetting(setting = setting)
        }

        // advertise network setting
        private fun syncAdvertiseNetworkConfig(config: JSONObject) {
            val igaWorks = config.toJSONObjectValue("igaworks")
            val manPlus = config.toJSONObjectValue("manPlus")
            val d = AdvertiseNetworkSettingEntity.empty()
            val setting = AdvertiseNetworkSettingEntity(
                igaWorks = AdvertiseNetworkSettingEntity.IGAWorks(
                    appKey = igaWorks?.toStringValue("appKey") ?: d.igaWorks.appKey,
                    hashKey = igaWorks?.toStringValue("hashKey") ?: d.igaWorks.hashKey
                ),
                manPlus = AdvertiseNetworkSettingEntity.ManPlus(
                    publisherCode = manPlus?.toIntValue("publisherCode") ?: d.manPlus.publisherCode,
                    mediaCode = manPlus?.toIntValue("mediaCode") ?: d.manPlus.mediaCode
                )
            )
            RemotePreferenceData.fetchAdvertiseNetworkSetting(setting = setting)
        }

        // in-app settings
        private fun syncInAppConfig(config: JSONObject) {
            val d = InAppSettingEntity.empty()
            // make value
            var rewardBannerDelay = d.main.rewardBannerDelay
            var rewardBanner = d.main.pid.rewardBanner
            var linearSSP_320X50 = d.main.pid.linearSSP_320x50
            var linearSSP_320X100 = d.main.pid.linearSSP_320x100
            var linearNative_320X50 = d.main.pid.linearNative_320x50
            var linearNative_320X100 = d.main.pid.linearNative_320x100
            config.toJSONObjectValue("main")?.let { main ->
                rewardBannerDelay = main.toLongValue("rewardBannerDelay", d.main.rewardBannerDelay)
                main.toJSONObjectValue("pid")?.let { pid ->
                    rewardBanner = pid.toStringValue("rewardBanner", d.main.pid.rewardBanner)
                    linearSSP_320X50 = pid.toStringValue("linearSSP", d.main.pid.linearSSP_320x50)
                    linearSSP_320X100 = pid.toStringValue("linearSSP_320x100", d.main.pid.linearSSP_320x100)
                    linearNative_320X50 = pid.toStringValue("linearNative", d.main.pid.linearNative_320x50)
                    linearNative_320X100 = pid.toStringValue("linearNative_320x100", d.main.pid.linearNative_320x100)
                }
            }
            // make model
            val setting = InAppSettingEntity(
                main = InAppSettingEntity.Main(
                    rewardBannerDelay = rewardBannerDelay,
                    pid = InAppSettingEntity.Main.PlacementID(
                        rewardBanner = rewardBanner,
                        linearSSP_320x50 = linearSSP_320X50,
                        linearSSP_320x100 = linearSSP_320X100,
                        linearNative_320x50 = linearNative_320X50,
                        linearNative_320x100 = linearNative_320X100
                    )
                )
            )
            RemotePreferenceData.fetchInAppSetting(setting = setting)
        }

        // touch ticket settings
        private fun syncTouchTicketConfig(config: JSONObject) {
            val d = TouchTicketSettingEntity.empty()
            // pop-ad
            val pop = config.toJSONObjectValue("popAD")
            val pid = config.toJSONObjectValue("pid")
            val setting = TouchTicketSettingEntity(
                period = config.toIntValue("period", d.period),
                limitCount = config.toIntValue("limitCount", d.limitCount),
                allowNoAd = config.toBooleanValue("allowNoAd", d.allowNoAd),
                popAD = TouchTicketSettingEntity.PopAD(
                    position = pop?.toFloatValue("position") ?: d.popAD.position,
                    interval = pop?.toIntValue("interval") ?: d.popAD.interval,
                    exclude = pop?.toBooleanValue("exclude") ?: d.popAD.exclude,
                    excludePosition = pop?.toFloatValue("excludePosition") ?: d.popAD.excludePosition
                ),
                pid = TouchTicketSettingEntity.PlacementID(
                    linearSSP_320x50 = pid?.toStringValue("linearSSP", d.pid.linearSSP_320x50) ?: d.pid.linearSSP_320x50,
                    linearSSP_320x100 = pid?.toStringValue("linearSSP_320x100", d.pid.linearSSP_320x100) ?: d.pid.linearSSP_320x100,
                    popupSSP = pid?.toStringValue("popupSSP", d.pid.popupSSP) ?: d.pid.popupSSP,
                    popupNative = pid?.toStringValue("popupNative", d.pid.popupNative) ?: d.pid.popupNative,
                    openInterstitialSSP = pid?.toStringValue("openInterstitialSSP", d.pid.openInterstitialSSP) ?: d.pid.openInterstitialSSP,
                    openInterstitialNative = pid?.toStringValue("openInterstitialNative", d.pid.openInterstitialNative) ?: d.pid.openInterstitialNative,
                    openInterstitialVideoSSP = pid?.toStringValue("openInterstitialVideoSSP", d.pid.openInterstitialVideoSSP) ?: d.pid.openInterstitialVideoSSP,
                    openBoxBannerSSP = pid?.toStringValue("openBoxBannerSSP", d.pid.openBoxBannerSSP) ?: d.pid.openBoxBannerSSP,
                    closeInterstitialSSP = pid?.toStringValue("closeInterstitialSSP", d.pid.closeInterstitialSSP) ?: d.pid.closeInterstitialSSP,
                    closeInterstitialNative = pid?.toStringValue("closeInterstitialNative", d.pid.closeInterstitialNative) ?: d.pid.closeInterstitialNative
                )
            )
            RemotePreferenceData.fetchTouchTicketSetting(setting = setting)
        }

        // video ticket setting
        private fun syncVideoTicketConfig(config: JSONObject) {
            val d = VideoTicketSettingEntity.empty()
            val pid = config.toJSONObjectValue("pid")
            // reward video
            val setting = VideoTicketSettingEntity(
                period = config.toIntValue("period", d.period),
                limitCount = config.toIntValue("limitCount", d.limitCount),
                pid = VideoTicketSettingEntity.PlacementID(
                    linearSSP_320x50 = pid?.toStringValue("linearSSP") ?: d.pid.linearSSP_320x50,
                    linearSSP_320x100 = pid?.toStringValue("linearSSP_320x100") ?: d.pid.linearSSP_320x100,
                    openRewardVideoSSP = pid?.toStringValue("openRewardVideoSSP") ?: d.pid.openRewardVideoSSP,
                    openInterstitialSSP = pid?.toStringValue("openInterstitialSSP") ?: d.pid.openInterstitialSSP,
                    openInterstitialNative = pid?.toStringValue("openInterstitialNative") ?: d.pid.openInterstitialNative,
                    openBoxBannerSSP = pid?.toStringValue("openBoxBannerSSP") ?: d.pid.openBoxBannerSSP,
                    closeInterstitialSSP = pid?.toStringValue("closeInterstitialSSP") ?: d.pid.closeInterstitialSSP,
                    closeInterstitialNative = pid?.toStringValue("closeInterstitialNative") ?: d.pid.closeInterstitialNative
                )
            )
            RemotePreferenceData.fetchVideoTicketSetting(setting = setting)
        }

        // ticket box setting
        private fun syncTicketBoxConfig(config: JSONObject) {
            val d = TicketBoxSettingEntity.empty()
            // pop-ad
            val pop = config.toJSONObjectValue("popAD")
            val pid = config.toJSONObjectValue("pid")
            val setting = TicketBoxSettingEntity(
                allowNoAd = config.toBooleanValue("allowNoAd", d.allowNoAd),
                popAD = TicketBoxSettingEntity.PopAD(
                    position = pop?.toFloatValue("position") ?: d.popAD.position,
                    interval = pop?.toIntValue("interval") ?: d.popAD.interval,
                    exclude = pop?.toBooleanValue("exclude") ?: d.popAD.exclude,
                    excludePosition = pop?.toFloatValue("excludePosition") ?: d.popAD.excludePosition
                ),
                pid = TicketBoxSettingEntity.PlacementID(
                    linearSSP_320x50 = pid?.toStringValue("linearSSP", d.pid.linearSSP_320x50) ?: d.pid.linearSSP_320x50,
                    linearSSP_320x100 = pid?.toStringValue("linearSSP_320x100", d.pid.linearSSP_320x100) ?: d.pid.linearSSP_320x100,
                    popupSSP = pid?.toStringValue("popupSSP", d.pid.popupSSP) ?: d.pid.popupSSP,
                    popupNative = pid?.toStringValue("popupNative", d.pid.popupNative) ?: d.pid.popupNative,
                    openInterstitialSSP = pid?.toStringValue("openInterstitialSSP", d.pid.openInterstitialSSP) ?: d.pid.openInterstitialSSP,
                    openInterstitialNative = pid?.toStringValue("openInterstitialNative", d.pid.openInterstitialNative) ?: d.pid.openInterstitialNative,
                    openInterstitialVideoSSP = pid?.toStringValue("openInterstitialVideoSSP", d.pid.openInterstitialVideoSSP) ?: d.pid.openInterstitialVideoSSP,
                    openBoxBannerSSP = pid?.toStringValue("openBoxBannerSSP", d.pid.openBoxBannerSSP) ?: d.pid.openBoxBannerSSP,
                    closeInterstitialSSP = pid?.toStringValue("closeInterstitialSSP", d.pid.closeInterstitialSSP) ?: d.pid.closeInterstitialSSP,
                    closeInterstitialNative = pid?.toStringValue("closeInterstitialNative", d.pid.closeInterstitialNative) ?: d.pid.closeInterstitialNative
                )
            )
            RemotePreferenceData.fetchTicketBoxSetting(setting = setting)
        }

        // mission setting
        private fun syncMissionConfig(config: JSONObject) {
            val setting = MissionSettingEntity(
                attendanceId = config.toStringValue("attendance")
            )
            RemotePreferenceData.fetchMissionSetting(setting = setting)
        }
    }
}