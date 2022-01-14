package com.avatye.cashblock.base

import android.app.Application
import androidx.annotation.Keep
import com.avatye.cashblock.base.component.contract.business.AccountContractor
import com.avatye.cashblock.base.component.domain.entity.app.AppEnvironment
import com.avatye.cashblock.base.component.domain.entity.app.AppInspection
import com.avatye.cashblock.base.component.domain.entity.user.Profile
import com.avatye.cashblock.base.component.support.*
import com.avatye.cashblock.base.library.LogHandler

internal const val MODULE_NAME = "Core@Block"

@Keep
object CoreConstants {
    internal const val CASHBLOCK_APP_ID = "cashblock.app.id"
    internal const val CASHBLOCK_APP_SECRET = "cashblock.app.secret"

    internal const val CASHBLOCK_CONFIG_LOG = "cashblock.config.log"
    internal const val CASHBLOCK_CONFIG_DEVELOPER = "cashblock.config.developer"
    internal const val CASHBLOCK_CONFIG_ENVIRONMENT = "cashblock.config.environment"

    // connector
    internal const val CASHBLOCK_CONNECT_ROULETTE = "com.avatye.cashblock.feature.roulette.block.Connector"
    internal const val CASHBLOCK_CONNECT_OFFERWALL = "com.avatye.cashblock.feature.offerwall.block.Connector"
    internal const val CASHBLOCK_CONNECT_PUBLISHER = "com.avatye.cashblock.publisher.Connector"

    // log - roulette
    const val CASHBLOCK_LOG_ROULETTE_ENTER = "view:roulette:enter"
    const val CASHBLOCK_LOG_ROULETTE_OFFERWALL_ENTER = "view:roulette-offerwall:enter"

    // log - offerwall
    const val CASHBLOCK_LOG_OFFERWALL_ENTER = "view:offerwall:enter"
}

@Keep
internal object Core {
    // logger
    val logger: LogHandler = LogHandler(moduleName = MODULE_NAME)

    // region # core base config
    lateinit var application: Application
        private set

    lateinit var appId: String
        private set

    lateinit var appSecret: String
        private set

    val isInitialized: Boolean
        get() {
            return Core::application.isInitialized
                    && Core::appId.isInitialized
                    && Core::appSecret.isInitialized
        }
    // endregion

    // region # environment
    val allowLog: Boolean by lazy {
        if (Core::application.isInitialized) {
            val logFlag = application.metaDataValue(CoreConstants.CASHBLOCK_CONFIG_LOG) ?: "false"
            logFlag.equals(other = "true", ignoreCase = true)
        } else {
            false
        }
    }

    val allowDeveloper: Boolean by lazy {
        if (Core::application.isInitialized) {
            val flag = application.metaDataValue(CoreConstants.CASHBLOCK_CONFIG_DEVELOPER) ?: "false"
            flag.equals(other = "true", ignoreCase = true)
        } else {
            false
        }
    }

    val appEnvironment: AppEnvironment by lazy {
        if (Core::application.isInitialized) {
            AppEnvironment.from(value = application.metaDataValue(CoreConstants.CASHBLOCK_CONFIG_ENVIRONMENT) ?: "live")
        } else {
            AppEnvironment.LIVE
        }
    }
    // endregion

    // region # config
    val appVersionCode: String by lazy {
        if (Core::application.isInitialized) application.hostAppVersionCode else ""
    }

    val appVersionName: String by lazy {
        if (Core::application.isInitialized) application.hostAppVersionName else ""
    }

    val appName: String by lazy {
        if (Core::application.isInitialized) application.hostAppName else ""
    }

    val appPackageName: String by lazy {
        if (Core::application.isInitialized) application.hostPackageName else ""
    }
    // endregion

    // region # core server config
    var appInspection: AppInspection? = null
        set(value) {
            if (field != value) {
                field = value
            }
        }
    // endregion

    // region # postback custom data
    var appCustomData: String? = null
    // endregion

    fun initialize(application: Application) {
        this.application = application
        // app-id
        this.application.metaDataValue(CoreConstants.CASHBLOCK_APP_ID).let {
            if (it.isNullOrEmpty()) {
                throw RuntimeException("${CoreConstants.CASHBLOCK_APP_ID} is null or empty")
            }
            this.appId = it
        }
        // app-secret
        this.application.metaDataValue(CoreConstants.CASHBLOCK_APP_SECRET).let {
            if (it.isNullOrEmpty()) {
                throw RuntimeException("${CoreConstants.CASHBLOCK_APP_SECRET} is null or empty")
            }
            this.appSecret = it
        }
        // channel talk
        ChannelTalkUtil.init(application = application)
    }

    fun initialize(application: Application, appId: String, appSecret: String) {
        this.application = application
        this.appId = appId
        this.appSecret = appSecret
        // channel talk
        ChannelTalkUtil.init(application = application)
    }

    fun setUserProfile(profile: Profile) = AccountContractor.setUserProfile(profile = profile)

    fun getUserProfile() = AccountContractor.userProfile
}
