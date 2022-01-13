package com.avatye.cashblock.base

import android.app.Application
import androidx.annotation.Keep
import com.avatye.cashblock.base.component.domain.entity.app.AppEnvironment
import com.avatye.cashblock.base.component.domain.entity.app.AppInspection
import com.avatye.cashblock.base.component.support.*
import com.avatye.cashblock.base.internal.controller.LoginController
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

    // log
    const val CASHBLOCK_LOG_ROULETTE_INTRO = "view:roulette:intro"
    const val CASHBLOCK_LOG_OFFERWALL_INTRO = "view:offerwall:intro"
}

@Keep
internal object Core {
    // logger
    internal val logger: LogHandler = LogHandler(moduleName = MODULE_NAME)

    // region # core base config
    internal lateinit var application: Application
        private set

    internal lateinit var appId: String
        private set

    internal lateinit var appSecret: String
        private set

    internal val isInitialized: Boolean
        get() {
            return Core::application.isInitialized
                    && Core::appId.isInitialized
                    && Core::appSecret.isInitialized
        }
    // endregion


    // region # environment
    internal val allowLog: Boolean by lazy {
        if (Core::application.isInitialized) {
            val logFlag = application.metaDataValue(CoreConstants.CASHBLOCK_CONFIG_LOG) ?: "false"
            logFlag.equals(other = "true", ignoreCase = true)
        } else {
            false
        }
    }

    internal val allowDeveloper: Boolean by lazy {
        if (Core::application.isInitialized) {
            val flag = application.metaDataValue(CoreConstants.CASHBLOCK_CONFIG_DEVELOPER) ?: "false"
            flag.equals(other = "true", ignoreCase = true)
        } else {
            false
        }
    }

    internal val appEnvironment: AppEnvironment by lazy {
        if (Core::application.isInitialized) {
            AppEnvironment.from(value = application.metaDataValue(CoreConstants.CASHBLOCK_CONFIG_ENVIRONMENT) ?: "live")
        } else {
            AppEnvironment.LIVE
        }
    }
    // endregion


    // region # config
    internal val appVersionCode: String by lazy {
        if (Core::application.isInitialized) application.hostAppVersionCode else ""
    }

    internal val appVersionName: String by lazy {
        if (Core::application.isInitialized) application.hostAppVersionName else ""
    }

    internal val appName: String by lazy {
        if (Core::application.isInitialized) application.hostAppName else ""
    }

    internal val appPackageName: String by lazy {
        if (Core::application.isInitialized) application.hostPackageName else ""
    }
    // endregion


    // region # core server config
    internal var appInspection: AppInspection? = null
        set(value) {
            if (field != value) {
                field = value
            }
        }
    // endregion


    // region # postback custom data
    internal var appCustomData: String? = null
    // endregion


    internal fun initialize(application: Application) {
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


    internal fun initialize(application: Application, appId: String, appSecret: String) {
        this.application = application
        this.appId = appId
        this.appSecret = appSecret
        // channel talk
        ChannelTalkUtil.init(application = application)
    }


    internal fun setAppUserId(appUserId: String) = LoginController.setLoginUserId(appUserId = appUserId)


    internal fun getAppUserId() = LoginController.getLoginUserId()
}
