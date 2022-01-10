package com.avatye.cashblock.base.internal.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.avatye.cashblock.BuildConfig
import com.avatye.cashblock.base.FeatureCore
import com.avatye.cashblock.base.component.domain.entity.setting.*

internal object RemotePreferenceData {

    // region { version }
    private var valueOfRemoteVersion = Preference.version
    val version: Long
        get() {
            return valueOfRemoteVersion
        }

    private var valueOfSDKVersion = Preference.sdkVersion
    val sdkVersion: Int
        get() {
            return valueOfSDKVersion
        }

    fun updateVersion(remoteVersion: Long? = null, sdkVersion: Int? = null) {
        remoteVersion?.let {
            valueOfRemoteVersion = it
            Preference.version = it
        }
        sdkVersion?.let {
            valueOfSDKVersion = it
            Preference.sdkVersion = it
        }
    }

    fun needUpdate(version: Long): Boolean {
        val settingVersion = valueOfRemoteVersion
        return (settingVersion == 0L
                || settingVersion != version
                || valueOfSDKVersion != BuildConfig.X_BUILD_SDK_VERSION_CODE)
    }
    // endregion


    // region { App Config }
    private var valueOfAppInfo = AppInfoSettingEntity(
        appName = Preference.AppConfig.appName,
        storeUrl = Preference.AppConfig.storeUrl,
        pointName = Preference.AppConfig.pointName,
        rouletteName = Preference.AppConfig.rouletteName,
        rouletteCampaign = Preference.AppConfig.rouletteCampaign,
        tooltipMessage = Preference.AppConfig.tooltipMessage,
        allowLinearAD = Preference.AppConfig.allowLinearAD,
        allowMoreMenu = Preference.AppConfig.allowMoreMenu,
        allowTicketBox = Preference.AppConfig.allowTicketBox,
        allowAgeVerification = Preference.AppConfig.appAllowAgeVerification
    )
    val appInfo: AppInfoSettingEntity
        get() {
            return valueOfAppInfo
        }

    fun fetchAppInfoSetting(setting: AppInfoSettingEntity) {
        Preference.AppConfig.fetch(setting)
        valueOfAppInfo = setting
    }
    // endregion


    // region { advertise network }
    private var valueOfAdvertiseNetwork = AdvertiseNetworkSettingEntity(
        igaWorks = AdvertiseNetworkSettingEntity.IGAWorks(
            appKey = Preference.AdvertiseNetworkConfig.IGAWorks.appKey,
            hashKey = Preference.AdvertiseNetworkConfig.IGAWorks.hashKey
        ),
        manPlus = AdvertiseNetworkSettingEntity.ManPlus(
            publisherCode = Preference.AdvertiseNetworkConfig.ManPlus.publisherCode,
            mediaCode = Preference.AdvertiseNetworkConfig.ManPlus.mediaCode
        )
    )
    val advertiseNetwork: AdvertiseNetworkSettingEntity
        get() {
            return valueOfAdvertiseNetwork
        }

    fun fetchAdvertiseNetworkSetting(setting: AdvertiseNetworkSettingEntity) {
        Preference.AdvertiseNetworkConfig.fetch(setting)
        valueOfAdvertiseNetwork = setting
    }
    // endregion


    // region { notification }
    private var valueOfNotification = NotificationSettingEntity(
        inducePeriod = Preference.NotificationConfig.inducePeriod,
        induceTicketCount = Preference.NotificationConfig.induceTicketCount
    )
    val notification: NotificationSettingEntity
        get() {
            return valueOfNotification
        }

    fun fetchNotificationSetting(setting: NotificationSettingEntity) {
        valueOfNotification = setting
        Preference.NotificationConfig.fetch(setting = setting)
    }
    // endregion


    // region { mission }
    private var valueOfMission = MissionSettingEntity(
        attendanceId = Preference.Mission.attendanceId
    )
    val mission: MissionSettingEntity
        get() {
            return valueOfMission
        }

    fun fetchMissionSetting(setting: MissionSettingEntity) {
        Preference.Mission.fetch(setting = setting)
        valueOfMission = setting
    }
    // endregion


    // region { in app }
    private var valueOfInAppSetting = InAppSettingEntity(
        main = InAppSettingEntity.Main(
            rewardBannerDelay = Preference.InAppConfig.Main.rewardBannerDelay,
            pid = InAppSettingEntity.Main.PlacementID(
                rewardBanner = Preference.InAppConfig.Main.PID.rewardBanner,
                linearSSP = Preference.InAppConfig.Main.PID.linearSSP,
                linearNative = Preference.InAppConfig.Main.PID.linearNative
            )
        )
    )
    val inApp: InAppSettingEntity
        get() {
            return valueOfInAppSetting
        }

    fun fetchInAppSetting(setting: InAppSettingEntity) {
        Preference.InAppConfig.fetch(setting = setting)
        valueOfInAppSetting = setting
    }
    // endregion


    // region { touch ticket }
    private var valueOfTouchTicketSetting = TouchTicketSettingEntity(
        period = Preference.TouchTicketConfig.period,
        limitCount = Preference.TouchTicketConfig.limitCount,
        allowNoAd = Preference.TouchTicketConfig.allowNoAd,
        popAD = TouchTicketSettingEntity.PopAD(
            interval = Preference.TouchTicketConfig.PopAD.interval,
            position = Preference.TouchTicketConfig.PopAD.position,
            exclude = Preference.TouchTicketConfig.PopAD.exclude,
            excludePosition = Preference.TouchTicketConfig.PopAD.excludePosition
        ),
        pid = TouchTicketSettingEntity.PlacementID(
            linearSSP = Preference.TouchTicketConfig.PID.linearSSP,
            popupSSP = Preference.TouchTicketConfig.PID.popupSSP,
            popupNative = Preference.TouchTicketConfig.PID.popupNative,
            openInterstitialSSP = Preference.TouchTicketConfig.PID.openInterstitialSSP,
            openInterstitialNative = Preference.TouchTicketConfig.PID.openInterstitialNative,
            openInterstitialVideoSSP = Preference.TouchTicketConfig.PID.openInterstitialVideoSSP,
            openBoxBannerSSP = Preference.TouchTicketConfig.PID.openBoxBannerSSP,
            closeInterstitialSSP = Preference.TouchTicketConfig.PID.closeInterstitialSSP,
            closeInterstitialNative = Preference.TouchTicketConfig.PID.closeInterstitialNative
        )
    )
    val touchTicket: TouchTicketSettingEntity
        get() {
            return valueOfTouchTicketSetting
        }

    fun fetchTouchTicketSetting(setting: TouchTicketSettingEntity) {
        Preference.TouchTicketConfig.fetch(setting = setting)
        valueOfTouchTicketSetting = setting
    }
    // endregion


    // region { video ticket }
    private var valueOfVideoTicketSetting = VideoTicketSettingEntity(
        period = Preference.VideoTicketConfig.period,
        limitCount = Preference.VideoTicketConfig.limitCount,
        pid = VideoTicketSettingEntity.PlacementID(
            linearSSP = Preference.VideoTicketConfig.PID.linearSSP,
            openRewardVideoSSP = Preference.VideoTicketConfig.PID.openRewardVideoSSP,
            openInterstitialSSP = Preference.VideoTicketConfig.PID.openInterstitialSSP,
            openInterstitialNative = Preference.VideoTicketConfig.PID.openInterstitialNative,
            openBoxBannerSSP = Preference.VideoTicketConfig.PID.openBoxBannerSSP,
            closeInterstitialSSP = Preference.VideoTicketConfig.PID.closeInterstitialSSP,
            closeInterstitialNative = Preference.VideoTicketConfig.PID.closeInterstitialNative
        )
    )
    val videoTicket: VideoTicketSettingEntity
        get() {
            return valueOfVideoTicketSetting
        }

    fun fetchVideoTicketSetting(setting: VideoTicketSettingEntity) {
        Preference.VideoTicketConfig.fetch(setting = setting)
        valueOfVideoTicketSetting = setting
    }
    // endregion


    // region { ticket box }
    private var valueOfTicketBoxSetting = TicketBoxSettingEntity(
        allowNoAd = Preference.TicketBoxConfig.allowNoAd,
        popAD = TicketBoxSettingEntity.PopAD(
            interval = Preference.TicketBoxConfig.PopAD.interval,
            position = Preference.TicketBoxConfig.PopAD.position,
            exclude = Preference.TicketBoxConfig.PopAD.exclude,
            excludePosition = Preference.TicketBoxConfig.PopAD.excludePosition
        ),
        pid = TicketBoxSettingEntity.PlacementID(
            linearSSP = Preference.TicketBoxConfig.PID.linearSSP,
            popupSSP = Preference.TicketBoxConfig.PID.popupSSP,
            popupNative = Preference.TicketBoxConfig.PID.popupNative,
            openInterstitialSSP = Preference.TicketBoxConfig.PID.openInterstitialSSP,
            openInterstitialNative = Preference.TicketBoxConfig.PID.openInterstitialNative,
            openInterstitialVideoSSP = Preference.TicketBoxConfig.PID.openInterstitialVideoSSP,
            openBoxBannerSSP = Preference.TicketBoxConfig.PID.openBoxBannerSSP,
            closeInterstitialSSP = Preference.TicketBoxConfig.PID.closeInterstitialSSP,
            closeInterstitialNative = Preference.TicketBoxConfig.PID.closeInterstitialNative
        )
    )
    val ticketBox: TicketBoxSettingEntity
        get() {
            return valueOfTicketBoxSetting
        }

    fun fetchTicketBoxSetting(setting: TicketBoxSettingEntity) {
        Preference.TicketBoxConfig.fetch(setting = setting)
        valueOfTicketBoxSetting = setting
    }
    // endregion


    private object Preference {
        private const val preferenceName = "cash-block:core:remote-setting"
        private val pref: SharedPreferences by lazy {
            FeatureCore.application.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        }

        private val VERSION = "version"
        var version: Long
            get() {
                return pref.getLong(VERSION, 0L)
            }
            set(value) {
                pref.edit { putLong(VERSION, value) }
            }

        private val SDK_VERSION = "sdk:version"
        var sdkVersion: Int
            get() {
                return pref.getInt(SDK_VERSION, 0)
            }
            set(value) {
                pref.edit { putInt(SDK_VERSION, value) }
            }

        object BlockConfig {

        }


        object AppConfig {
            private const val APP_NAME = "app:app-name"
            private const val APP_STORE_URL = "app:store-url"
            private const val APP_POINT_NAME = "app:point-name"
            private const val APP_ROULETTE_NAME = "app:roulette-name"
            private const val APP_ROULETTE_CAMPAIGN = "app:roulette-campaign"
            private const val APP_TOOLTIP_MESSAGE = "app:tooltip-message"
            private const val APP_ALLOW_LINEAR_AD = "app:allow-linear-ad"
            private const val APP_ALLOW_MORE_MENU = "app:allow-more-menu"
            private const val APP_ALLOW_TICKET_BOX = "app:allow-ticket-box"
            private const val APP_ALLOW_AGE_VERIFICATION = "app:allow-age-verification"
            private val D = AppInfoSettingEntity.empty()

            val appName: String
                get() {
                    return pref.getString(APP_NAME, D.appName) ?: D.appName
                }

            val storeUrl: String
                get() {
                    return pref.getString(APP_STORE_URL, D.storeUrl) ?: D.storeUrl
                }

            val pointName: String
                get() {
                    return pref.getString(APP_POINT_NAME, D.pointName) ?: D.pointName
                }

            val rouletteName: String
                get() {
                    return pref.getString(APP_ROULETTE_NAME, D.rouletteName) ?: D.rouletteName
                }

            val rouletteCampaign: String
                get() {
                    return pref.getString(APP_ROULETTE_CAMPAIGN, D.rouletteCampaign) ?: D.rouletteCampaign
                }

            val tooltipMessage: String
                get() {
                    return pref.getString(APP_TOOLTIP_MESSAGE, D.tooltipMessage) ?: D.tooltipMessage
                }

            val allowLinearAD: Boolean
                get() {
                    return pref.getBoolean(APP_ALLOW_LINEAR_AD, D.allowLinearAD)
                }

            val allowMoreMenu: Boolean
                get() {
                    return pref.getBoolean(APP_ALLOW_MORE_MENU, D.allowMoreMenu)
                }

            val allowTicketBox: Boolean
                get() {
                    return pref.getBoolean(APP_ALLOW_TICKET_BOX, D.allowTicketBox)
                }

            val appAllowAgeVerification: Boolean
                get() {
                    return pref.getBoolean(APP_ALLOW_AGE_VERIFICATION, D.allowAgeVerification)
                }

            fun fetch(setting: AppInfoSettingEntity) {
                pref.edit {
                    putString(APP_NAME, setting.appName)
                    putString(APP_STORE_URL, setting.storeUrl)
                    putString(APP_POINT_NAME, setting.pointName)
                    putString(APP_ROULETTE_NAME, setting.rouletteName)
                    putString(APP_ROULETTE_CAMPAIGN, setting.rouletteCampaign)
                    putString(APP_TOOLTIP_MESSAGE, setting.tooltipMessage)
                    putBoolean(APP_ALLOW_LINEAR_AD, setting.allowLinearAD)
                    putBoolean(APP_ALLOW_MORE_MENU, setting.allowMoreMenu)
                    putBoolean(APP_ALLOW_TICKET_BOX, setting.allowTicketBox)
                    putBoolean(APP_ALLOW_AGE_VERIFICATION, setting.allowAgeVerification)
                }
            }

            fun clear() {
                arrayOf(
                    APP_NAME,
                    APP_STORE_URL,
                    APP_POINT_NAME,
                    APP_ROULETTE_NAME,
                    APP_ROULETTE_CAMPAIGN,
                    APP_TOOLTIP_MESSAGE,
                    APP_ALLOW_LINEAR_AD,
                    APP_ALLOW_MORE_MENU,
                    APP_ALLOW_TICKET_BOX,
                    APP_ALLOW_AGE_VERIFICATION
                ).forEach {
                    pref.edit { remove(it) }
                }
            }
        }


        object AdvertiseNetworkConfig {
            private const val ADNETWORK_IGAWORKS_APP_KEY = "ad-network:iga-works:app-key"
            private const val ADNETWORK_IGAWORKS_HASH_KEY = "ad-network:iga-works:hash-key"
            private const val ADNETWORK_MANPLUS_PUBLISHER_CODE = "ad-network:man-plus:publisher-code"
            private const val ADNETWORK_MANPLUS_MEDIA_CODE = "ad-network:man-plus:media-code"
            private val D = AdvertiseNetworkSettingEntity.empty()

            object IGAWorks {
                val appKey: String
                    get() {
                        return pref.getString(ADNETWORK_IGAWORKS_APP_KEY, D.igaWorks.appKey) ?: D.igaWorks.appKey
                    }

                val hashKey: String
                    get() {
                        return pref.getString(ADNETWORK_IGAWORKS_HASH_KEY, D.igaWorks.hashKey) ?: D.igaWorks.hashKey
                    }
            }

            object ManPlus {
                val publisherCode: Int
                    get() {
                        return pref.getInt(ADNETWORK_MANPLUS_PUBLISHER_CODE, D.manPlus.publisherCode)
                    }

                val mediaCode: Int
                    get() {
                        return pref.getInt(ADNETWORK_MANPLUS_MEDIA_CODE, D.manPlus.mediaCode)
                    }
            }

            fun fetch(setting: AdvertiseNetworkSettingEntity) {
                pref.edit {
                    // iga-works
                    putString(ADNETWORK_IGAWORKS_APP_KEY, setting.igaWorks.appKey)
                    putString(ADNETWORK_IGAWORKS_HASH_KEY, setting.igaWorks.hashKey)
                    // man-plus
                    putInt(ADNETWORK_MANPLUS_PUBLISHER_CODE, setting.manPlus.publisherCode)
                    putInt(ADNETWORK_MANPLUS_MEDIA_CODE, setting.manPlus.mediaCode)
                }
            }

            fun clear() {
                arrayOf(
                    ADNETWORK_IGAWORKS_APP_KEY,
                    ADNETWORK_IGAWORKS_HASH_KEY,
                    ADNETWORK_MANPLUS_PUBLISHER_CODE,
                    ADNETWORK_MANPLUS_MEDIA_CODE
                ).forEach {
                    pref.edit { remove(it) }
                }
            }
        }


        object NotificationConfig {
            private const val NOTIFICATION_INDUCE_PERIOD = "notification:induce-period"
            private const val NOTIFICATION_INDUCE_TICKET_COUNT = "notification:induce-ticket-count"
            private val D = NotificationSettingEntity.empty()

            val inducePeriod: Long
                get() {
                    return pref.getLong(NOTIFICATION_INDUCE_PERIOD, D.inducePeriod)
                }

            val induceTicketCount: Int
                get() {
                    return pref.getInt(NOTIFICATION_INDUCE_TICKET_COUNT, D.induceTicketCount)
                }

            fun fetch(setting: NotificationSettingEntity) {
                pref.edit {
                    putLong(NOTIFICATION_INDUCE_PERIOD, setting.inducePeriod)
                    putInt(NOTIFICATION_INDUCE_TICKET_COUNT, setting.induceTicketCount)
                }
            }

            fun clear() {
                arrayOf(
                    NOTIFICATION_INDUCE_PERIOD,
                    NOTIFICATION_INDUCE_TICKET_COUNT
                ).forEach {
                    pref.edit { remove(it) }
                }
            }
        }


        object Mission {
            private const val MISSION_ATTENDANCE = "mission:attendance-id"

            val attendanceId: String
                get() {
                    return pref.getString(MISSION_ATTENDANCE, "") ?: ""
                }

            fun fetch(setting: MissionSettingEntity) {
                pref.edit {
                    putString(MISSION_ATTENDANCE, setting.attendanceId)
                }
            }

            fun clear() {
                arrayOf(MISSION_ATTENDANCE).forEach {
                    pref.edit { remove(it) }
                }
            }
        }


        object InAppConfig {
            private const val IN_APP_MAIN_REWARD_BANNER_DELAY = "inapp:main:reward-banner-delay"
            private const val IN_APP_MAIN_PID_REWARD_BANNER = "inapp:main:pid:reward-banner"
            private const val IN_APP_MAIN_PID_LINEAR_SSP = "inapp:main:pid:linear-ssp"
            private const val IN_APP_MAIN_PID_LINEAR_NATIVE = "inapp:main:pid:linear-native"
            private val D = InAppSettingEntity.empty()

            object Main {
                val rewardBannerDelay: Long
                    get() {
                        return pref.getLong(IN_APP_MAIN_REWARD_BANNER_DELAY, D.main.rewardBannerDelay)
                    }

                object PID {
                    val rewardBanner: String
                        get() {
                            return pref.getString(IN_APP_MAIN_PID_REWARD_BANNER, D.main.pid.rewardBanner) ?: D.main.pid.rewardBanner
                        }

                    val linearSSP: String
                        get() {
                            return pref.getString(IN_APP_MAIN_PID_LINEAR_SSP, D.main.pid.linearSSP) ?: D.main.pid.linearSSP
                        }

                    val linearNative: String
                        get() {
                            return pref.getString(IN_APP_MAIN_PID_LINEAR_NATIVE, D.main.pid.linearNative) ?: D.main.pid.linearNative
                        }
                }
            }

            fun fetch(setting: InAppSettingEntity) {
                pref.edit {
                    putLong(IN_APP_MAIN_REWARD_BANNER_DELAY, setting.main.rewardBannerDelay)
                    putString(IN_APP_MAIN_PID_REWARD_BANNER, setting.main.pid.rewardBanner)
                    putString(IN_APP_MAIN_PID_LINEAR_SSP, setting.main.pid.linearSSP)
                    putString(IN_APP_MAIN_PID_LINEAR_NATIVE, setting.main.pid.linearNative)
                }
            }

            fun clear() {
                arrayOf(
                    IN_APP_MAIN_PID_REWARD_BANNER,
                    IN_APP_MAIN_PID_LINEAR_SSP,
                    IN_APP_MAIN_PID_LINEAR_NATIVE
                ).forEach {
                    pref.edit { remove(it) }
                }
            }
        }


        object TouchTicketConfig {
            private const val TOUCH_TICKET_PERIOD = "touch-ticket:period"
            private const val TOUCH_TICKET_LIMIT_COUNT = "touch-ticket:limit-count"
            private const val TOUCH_TICKET_ALLOW_NO_AD = "touch-ticket:allow-no-ad"

            // popup ad
            private const val TOUCH_TICKET_POP_AD_INTERVAL = "touch-ticket:pop-ad:interval"
            private const val TOUCH_TICKET_POP_AD_POSITION = "touch-ticket:pop-ad:position"
            private const val TOUCH_TICKET_POP_AD_EXCLUDE = "touch-ticket:pop-ad:exclude"
            private const val TOUCH_TICKET_POP_AD_EXCLUDE_POSITION = "touch-ticket:pop-ad:exclude-position"

            // placement id
            private const val TOUCH_TICKET_PID_LINEAR_SSP = "touch-ticket:pid:linear-ssp"
            private const val TOUCH_TICKET_PID_POPUP_SSP = "touch-ticket:pid:popup-ssp"
            private const val TOUCH_TICKET_PID_POPUP_NATIVE = "touch-ticket:pid:popup-native"
            private const val TOUCH_TICKET_PID_OPEN_INTERSTITIAL_SSP = "touch-ticket:pid:open-interstitial-ssp"
            private const val TOUCH_TICKET_PID_OPEN_INTERSTITIAL_NATIVE = "touch-ticket:pid:open-interstitial-native"
            private const val TOUCH_TICKET_PID_OPEN_INTERSTITIAL_VIDEO_SSP = "touch-ticket:pid:open-interstitial-video-ssp"
            private const val TOUCH_TICKET_PID_OPEN_BOX_BANNER_SSP = "touch-ticket:pid:open-box-banner-ssp"
            private const val TOUCH_TICKET_PID_CLOSE_INTERSTITIAL_SSP = "touch-ticket:pid:close-interstitial-ssp"
            private const val TOUCH_TICKET_PID_CLOSE_INTERSTITIAL_NATIVE = "touch-ticket:pid:close-interstitial-native"

            // default
            private val D = TouchTicketSettingEntity.empty()

            val period: Int
                get() {
                    return pref.getInt(TOUCH_TICKET_PERIOD, D.period)
                }


            val limitCount: Int
                get() {
                    return pref.getInt(TOUCH_TICKET_LIMIT_COUNT, D.limitCount)
                }


            val allowNoAd: Boolean
                get() {
                    return pref.getBoolean(TOUCH_TICKET_ALLOW_NO_AD, D.allowNoAd)
                }


            object PopAD {
                val interval: Int
                    get() {
                        return pref.getInt(TOUCH_TICKET_POP_AD_INTERVAL, D.popAD.interval)
                    }


                val position: Float
                    get() {
                        return pref.getFloat(TOUCH_TICKET_POP_AD_POSITION, D.popAD.position)
                    }

                val exclude: Boolean
                    get() {
                        return pref.getBoolean(TOUCH_TICKET_POP_AD_EXCLUDE, D.popAD.exclude)
                    }

                val excludePosition: Float
                    get() {
                        return pref.getFloat(TOUCH_TICKET_POP_AD_EXCLUDE_POSITION, D.popAD.excludePosition)
                    }
            }


            object PID {
                val linearSSP: String
                    get() {
                        return pref.getString(TOUCH_TICKET_PID_LINEAR_SSP, D.pid.linearSSP) ?: D.pid.linearSSP
                    }


                val popupSSP: String
                    get() {
                        return pref.getString(TOUCH_TICKET_PID_POPUP_SSP, D.pid.popupSSP) ?: D.pid.popupSSP
                    }


                val popupNative: String
                    get() {
                        return pref.getString(TOUCH_TICKET_PID_POPUP_NATIVE, D.pid.popupNative) ?: D.pid.popupNative
                    }


                val openInterstitialVideoSSP: String
                    get() {
                        return pref.getString(TOUCH_TICKET_PID_OPEN_INTERSTITIAL_VIDEO_SSP, D.pid.openInterstitialVideoSSP)
                            ?: D.pid.openInterstitialVideoSSP
                    }


                val openInterstitialSSP: String
                    get() {
                        return pref.getString(TOUCH_TICKET_PID_OPEN_INTERSTITIAL_SSP, D.pid.openInterstitialSSP)
                            ?: D.pid.openInterstitialSSP
                    }


                val openInterstitialNative: String
                    get() {
                        return pref.getString(TOUCH_TICKET_PID_OPEN_INTERSTITIAL_NATIVE, D.pid.openInterstitialNative)
                            ?: D.pid.openInterstitialNative
                    }


                val openBoxBannerSSP: String
                    get() {
                        return pref.getString(TOUCH_TICKET_PID_OPEN_BOX_BANNER_SSP, D.pid.openBoxBannerSSP) ?: D.pid.openBoxBannerSSP
                    }


                val closeInterstitialSSP: String
                    get() {
                        return pref.getString(TOUCH_TICKET_PID_CLOSE_INTERSTITIAL_SSP, D.pid.closeInterstitialSSP)
                            ?: D.pid.closeInterstitialSSP
                    }


                val closeInterstitialNative: String
                    get() {
                        return pref.getString(TOUCH_TICKET_PID_CLOSE_INTERSTITIAL_NATIVE, D.pid.closeInterstitialNative)
                            ?: D.pid.closeInterstitialNative
                    }
            }


            fun fetch(setting: TouchTicketSettingEntity) {
                pref.edit {
                    // config
                    putInt(TOUCH_TICKET_PERIOD, setting.period)
                    putInt(TOUCH_TICKET_LIMIT_COUNT, setting.limitCount)
                    putBoolean(TOUCH_TICKET_ALLOW_NO_AD, setting.allowNoAd)
                    // popup ad
                    putInt(TOUCH_TICKET_POP_AD_INTERVAL, setting.popAD.interval)
                    putFloat(TOUCH_TICKET_POP_AD_POSITION, setting.popAD.position)
                    putBoolean(TOUCH_TICKET_POP_AD_EXCLUDE, setting.popAD.exclude)
                    putFloat(TOUCH_TICKET_POP_AD_EXCLUDE_POSITION, setting.popAD.excludePosition)
                    // placement id
                    putString(TOUCH_TICKET_PID_LINEAR_SSP, setting.pid.linearSSP)
                    putString(TOUCH_TICKET_PID_POPUP_SSP, setting.pid.popupSSP)
                    putString(TOUCH_TICKET_PID_POPUP_NATIVE, setting.pid.popupNative)
                    putString(TOUCH_TICKET_PID_OPEN_INTERSTITIAL_SSP, setting.pid.openInterstitialSSP)
                    putString(TOUCH_TICKET_PID_OPEN_INTERSTITIAL_NATIVE, setting.pid.openInterstitialNative)
                    putString(TOUCH_TICKET_PID_OPEN_INTERSTITIAL_VIDEO_SSP, setting.pid.openInterstitialVideoSSP)
                    putString(TOUCH_TICKET_PID_OPEN_BOX_BANNER_SSP, setting.pid.openBoxBannerSSP)
                    putString(TOUCH_TICKET_PID_CLOSE_INTERSTITIAL_SSP, setting.pid.closeInterstitialSSP)
                    putString(TOUCH_TICKET_PID_CLOSE_INTERSTITIAL_NATIVE, setting.pid.closeInterstitialNative)
                }
            }


            fun clear() {
                arrayOf(
                    TOUCH_TICKET_PERIOD,
                    TOUCH_TICKET_LIMIT_COUNT,
                    TOUCH_TICKET_ALLOW_NO_AD,
                    TOUCH_TICKET_POP_AD_INTERVAL,
                    TOUCH_TICKET_POP_AD_POSITION,
                    TOUCH_TICKET_POP_AD_EXCLUDE,
                    TOUCH_TICKET_POP_AD_EXCLUDE_POSITION,
                    TOUCH_TICKET_PID_LINEAR_SSP,
                    TOUCH_TICKET_PID_POPUP_SSP,
                    TOUCH_TICKET_PID_POPUP_NATIVE,
                    TOUCH_TICKET_PID_OPEN_INTERSTITIAL_SSP,
                    TOUCH_TICKET_PID_OPEN_INTERSTITIAL_NATIVE,
                    TOUCH_TICKET_PID_OPEN_INTERSTITIAL_VIDEO_SSP,
                    TOUCH_TICKET_PID_OPEN_BOX_BANNER_SSP,
                    TOUCH_TICKET_PID_CLOSE_INTERSTITIAL_SSP,
                    TOUCH_TICKET_PID_CLOSE_INTERSTITIAL_NATIVE
                ).forEach {
                    pref.edit { remove(it) }
                }
            }
        }


        object VideoTicketConfig {
            private const val VIDEO_TICKET_PERIOD = "video-ticket:period"
            private const val VIDEO_TICKET_LIMIT_COUNT = "video-ticket:limit-count"

            // placement id
            private const val VIDEO_TICKET_PID_LINEAR_SSP = "video-ticket:pid:linear-ssp"
            private const val VIDEO_TICKET_PID_OPEN_REWARD_VIDEO_SSP = "video-ticket:pid:open-reward-video-ssp"
            private const val VIDEO_TICKET_PID_OPEN_INTERSTITIAL_SSP = "video-ticket:pid:open-interstitial-ssp"
            private const val VIDEO_TICKET_PID_OPEN_INTERSTITIAL_NATIVE = "video-ticket:pid:open-interstitial-native"
            private const val VIDEO_TICKET_PID_OPEN_BOX_BANNER_SSP = "video-ticket:pid:open-box-banner-ssp"
            private const val VIDEO_TICKET_PID_CLOSE_INTERSTITIAL_SSP = "video-ticket:pid:close-interstitial-ssp"
            private const val VIDEO_TICKET_PID_CLOSE_INTERSTITIAL_NATIVE = "video-ticket:pid:close-interstitial-native"

            // default
            private val D = VideoTicketSettingEntity.empty()

            val period: Int
                get() {
                    return pref.getInt(VIDEO_TICKET_PERIOD, D.period)
                }


            val limitCount: Int
                get() {
                    return pref.getInt(VIDEO_TICKET_LIMIT_COUNT, D.limitCount)
                }


            object PID {
                val linearSSP: String
                    get() {
                        return pref.getString(VIDEO_TICKET_PID_LINEAR_SSP, D.pid.linearSSP) ?: D.pid.linearSSP
                    }


                val openRewardVideoSSP: String
                    get() {
                        return pref.getString(VIDEO_TICKET_PID_OPEN_REWARD_VIDEO_SSP, D.pid.openRewardVideoSSP)
                            ?: D.pid.openRewardVideoSSP
                    }


                val openInterstitialSSP: String
                    get() {
                        return pref.getString(VIDEO_TICKET_PID_OPEN_INTERSTITIAL_SSP, D.pid.openInterstitialSSP)
                            ?: D.pid.openInterstitialSSP
                    }


                val openInterstitialNative: String
                    get() {
                        return pref.getString(VIDEO_TICKET_PID_OPEN_INTERSTITIAL_NATIVE, D.pid.openInterstitialNative)
                            ?: D.pid.openInterstitialNative
                    }


                val openBoxBannerSSP: String
                    get() {
                        return pref.getString(VIDEO_TICKET_PID_OPEN_BOX_BANNER_SSP, D.pid.openBoxBannerSSP) ?: D.pid.openBoxBannerSSP
                    }


                val closeInterstitialSSP: String
                    get() {
                        return pref.getString(VIDEO_TICKET_PID_CLOSE_INTERSTITIAL_SSP, D.pid.closeInterstitialSSP)
                            ?: D.pid.closeInterstitialSSP
                    }


                val closeInterstitialNative: String
                    get() {
                        return pref.getString(VIDEO_TICKET_PID_CLOSE_INTERSTITIAL_NATIVE, D.pid.closeInterstitialNative)
                            ?: D.pid.closeInterstitialNative
                    }
            }


            fun fetch(setting: VideoTicketSettingEntity) {
                pref.edit {
                    putInt(VIDEO_TICKET_PERIOD, setting.period)
                    putInt(VIDEO_TICKET_LIMIT_COUNT, setting.limitCount)
                    // pid
                    putString(VIDEO_TICKET_PID_LINEAR_SSP, setting.pid.linearSSP)
                    putString(VIDEO_TICKET_PID_OPEN_REWARD_VIDEO_SSP, setting.pid.openRewardVideoSSP)
                    putString(VIDEO_TICKET_PID_OPEN_INTERSTITIAL_SSP, setting.pid.openInterstitialSSP)
                    putString(VIDEO_TICKET_PID_OPEN_INTERSTITIAL_NATIVE, setting.pid.openInterstitialNative)
                    putString(VIDEO_TICKET_PID_OPEN_BOX_BANNER_SSP, setting.pid.openBoxBannerSSP)
                    putString(VIDEO_TICKET_PID_CLOSE_INTERSTITIAL_SSP, setting.pid.closeInterstitialSSP)
                    putString(VIDEO_TICKET_PID_CLOSE_INTERSTITIAL_NATIVE, setting.pid.closeInterstitialNative)
                }
            }

            fun clear() {
                arrayOf(
                    VIDEO_TICKET_PERIOD,
                    VIDEO_TICKET_LIMIT_COUNT,
                    VIDEO_TICKET_PID_LINEAR_SSP,
                    VIDEO_TICKET_PID_OPEN_REWARD_VIDEO_SSP,
                    VIDEO_TICKET_PID_OPEN_INTERSTITIAL_SSP,
                    VIDEO_TICKET_PID_OPEN_INTERSTITIAL_NATIVE,
                    VIDEO_TICKET_PID_OPEN_BOX_BANNER_SSP,
                    VIDEO_TICKET_PID_CLOSE_INTERSTITIAL_SSP,
                    VIDEO_TICKET_PID_CLOSE_INTERSTITIAL_NATIVE
                ).forEach {
                    pref.edit {
                        remove(it)
                    }
                }
            }
        }


        object TicketBoxConfig {
            private const val TICKET_BOX_ALLOW_NO_AD = "ticket-box:allow-no-ad"

            // popup ad
            private const val TICKET_BOX_POP_AD_INTERVAL = "ticket-box:pop-ad:interval"
            private const val TICKET_BOX_POP_AD_POSITION = "ticket-box:pop-ad:position"
            private const val TICKET_BOX_POP_AD_EXCLUDE = "ticket-box:pop-ad:exclude"
            private const val TICKET_BOX_POP_AD_EXCLUDE_POSITION = "ticket-box:pop-ad:exclude-position"

            // placement id
            private const val TICKET_BOX_PID_LINEAR_SSP = "ticket-box:pid:linear-ssp"
            private const val TICKET_BOX_PID_POPUP_SSP = "ticket-box:pid:popup-ssp"
            private const val TICKET_BOX_PID_POPUP_NATIVE = "ticket-box:pid:popup-native"
            private const val TICKET_BOX_PID_OPEN_INTERSTITIAL_SSP = "ticket-box:pid:open-interstitial-ssp"
            private const val TICKET_BOX_PID_OPEN_INTERSTITIAL_NATIVE = "ticket-box:pid:open-interstitial-native"
            private const val TICKET_BOX_PID_OPEN_INTERSTITIAL_VIDEO_SSP = "ticket-box:pid:open-interstitial-video-ssp"
            private const val TICKET_BOX_PID_OPEN_BOX_BANNER_SSP = "ticket-box:pid:open-box-banner-ssp"
            private const val TICKET_BOX_PID_CLOSE_INTERSTITIAL_SSP = "ticket-box:pid:close-interstitial-ssp"
            private const val TICKET_BOX_PID_CLOSE_INTERSTITIAL_NATIVE = "ticket-box:pid:close-interstitial-native"

            // default
            private val D = TicketBoxSettingEntity.empty()

            val allowNoAd: Boolean
                get() {
                    return pref.getBoolean(TICKET_BOX_ALLOW_NO_AD, D.allowNoAd)
                }


            object PopAD {
                val interval: Int
                    get() {
                        return pref.getInt(TICKET_BOX_POP_AD_INTERVAL, D.popAD.interval)
                    }

                val position: Float
                    get() {
                        return pref.getFloat(TICKET_BOX_POP_AD_POSITION, D.popAD.position)
                    }

                val exclude: Boolean
                    get() {
                        return pref.getBoolean(TICKET_BOX_POP_AD_EXCLUDE, D.popAD.exclude)
                    }

                val excludePosition: Float
                    get() {
                        return pref.getFloat(TICKET_BOX_POP_AD_EXCLUDE_POSITION, D.popAD.excludePosition)
                    }
            }


            object PID {
                val linearSSP: String
                    get() {
                        return pref.getString(TICKET_BOX_PID_LINEAR_SSP, D.pid.linearSSP) ?: D.pid.linearSSP
                    }


                val popupSSP: String
                    get() {
                        return pref.getString(TICKET_BOX_PID_POPUP_SSP, D.pid.popupSSP) ?: D.pid.popupSSP
                    }


                val popupNative: String
                    get() {
                        return pref.getString(TICKET_BOX_PID_POPUP_NATIVE, D.pid.popupNative) ?: D.pid.popupNative
                    }


                val openInterstitialVideoSSP: String
                    get() {
                        return pref.getString(TICKET_BOX_PID_OPEN_INTERSTITIAL_VIDEO_SSP, D.pid.openInterstitialVideoSSP)
                            ?: D.pid.openInterstitialVideoSSP
                    }


                val openInterstitialSSP: String
                    get() {
                        return pref.getString(TICKET_BOX_PID_OPEN_INTERSTITIAL_SSP, D.pid.openInterstitialSSP)
                            ?: D.pid.openInterstitialSSP
                    }


                val openInterstitialNative: String
                    get() {
                        return pref.getString(TICKET_BOX_PID_OPEN_INTERSTITIAL_NATIVE, D.pid.openInterstitialNative)
                            ?: D.pid.openInterstitialNative
                    }


                val openBoxBannerSSP: String
                    get() {
                        return pref.getString(TICKET_BOX_PID_OPEN_BOX_BANNER_SSP, D.pid.openBoxBannerSSP) ?: D.pid.openBoxBannerSSP
                    }


                val closeInterstitialSSP: String
                    get() {
                        return pref.getString(TICKET_BOX_PID_CLOSE_INTERSTITIAL_SSP, D.pid.closeInterstitialSSP)
                            ?: D.pid.closeInterstitialSSP
                    }


                val closeInterstitialNative: String
                    get() {
                        return pref.getString(TICKET_BOX_PID_CLOSE_INTERSTITIAL_NATIVE, D.pid.closeInterstitialNative)
                            ?: D.pid.closeInterstitialNative
                    }
            }


            fun fetch(setting: TicketBoxSettingEntity) {
                pref.edit {
                    // config
                    putBoolean(TICKET_BOX_ALLOW_NO_AD, setting.allowNoAd)
                    // popup ad
                    putInt(TICKET_BOX_POP_AD_INTERVAL, setting.popAD.interval)
                    putFloat(TICKET_BOX_POP_AD_POSITION, setting.popAD.position)
                    putBoolean(TICKET_BOX_POP_AD_EXCLUDE, setting.popAD.exclude)
                    putFloat(TICKET_BOX_POP_AD_EXCLUDE_POSITION, setting.popAD.excludePosition)
                    // placement id
                    putString(TICKET_BOX_PID_LINEAR_SSP, setting.pid.linearSSP)
                    putString(TICKET_BOX_PID_POPUP_SSP, setting.pid.popupSSP)
                    putString(TICKET_BOX_PID_POPUP_NATIVE, setting.pid.popupNative)
                    putString(TICKET_BOX_PID_OPEN_INTERSTITIAL_SSP, setting.pid.openInterstitialSSP)
                    putString(TICKET_BOX_PID_OPEN_INTERSTITIAL_NATIVE, setting.pid.openInterstitialNative)
                    putString(TICKET_BOX_PID_OPEN_INTERSTITIAL_VIDEO_SSP, setting.pid.openInterstitialVideoSSP)
                    putString(TICKET_BOX_PID_OPEN_BOX_BANNER_SSP, setting.pid.openBoxBannerSSP)
                    putString(TICKET_BOX_PID_CLOSE_INTERSTITIAL_SSP, setting.pid.closeInterstitialSSP)
                    putString(TICKET_BOX_PID_CLOSE_INTERSTITIAL_NATIVE, setting.pid.closeInterstitialNative)
                }
            }


            fun clear() {
                arrayOf(
                    TICKET_BOX_ALLOW_NO_AD,
                    TICKET_BOX_POP_AD_INTERVAL,
                    TICKET_BOX_POP_AD_POSITION,
                    TICKET_BOX_POP_AD_EXCLUDE,
                    TICKET_BOX_POP_AD_EXCLUDE_POSITION,
                    TICKET_BOX_PID_LINEAR_SSP,
                    TICKET_BOX_PID_POPUP_SSP,
                    TICKET_BOX_PID_POPUP_NATIVE,
                    TICKET_BOX_PID_OPEN_INTERSTITIAL_SSP,
                    TICKET_BOX_PID_OPEN_INTERSTITIAL_NATIVE,
                    TICKET_BOX_PID_OPEN_INTERSTITIAL_VIDEO_SSP,
                    TICKET_BOX_PID_OPEN_BOX_BANNER_SSP,
                    TICKET_BOX_PID_CLOSE_INTERSTITIAL_SSP,
                    TICKET_BOX_PID_CLOSE_INTERSTITIAL_NATIVE
                ).forEach {
                    pref.edit { remove(it) }
                }
            }
        }
    }

}