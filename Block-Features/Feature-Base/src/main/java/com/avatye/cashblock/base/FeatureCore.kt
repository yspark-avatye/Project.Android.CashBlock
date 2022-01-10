package com.avatye.cashblock.base

import android.app.Application
import androidx.annotation.Keep
import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.entity.app.AppInspection
import com.avatye.cashblock.base.component.support.*
import com.avatye.cashblock.base.internal.controller.LoginController
import com.avatye.cashblock.base.internal.server.serve.ServeEnvironment
import com.avatye.cashblock.base.library.LogHandler

internal const val MODULE_NAME = "Feature-Core"

@Keep
object FeatureCore {
    // region # const
    private const val CASHBLOCK_CONFIG_LOG = "cashblock.config.log"
    private const val CASHBLOCK_CONFIG_DEVELOPER = "cashblock.config.developer"
    private const val CASHBLOCK_CONFIG_ENVIRONMENT = "cashblock.config.environment"

    // kyes
    const val CASHBLOCK_KEY_ROULETTE = "cashblock.app.key"
    const val CASHBLOCK_KEY_OFFERWALL = "cashblock.app.key.offerwall"
    const val CASHBLOCK_KEY_NEWSPICK = "cashblock.app.key.newspick"
    const val CASHBLOCK_KEY_GAME = "cashblock.app.key.game"

    // log
    const val CASHBLOCK_LOG_ROULETTE_INTRO = "view:roulette:intro"
    const val CASHBLOCK_LOG_OFFERWALL_INTRO = "view:offerwall:intro"

    // connector
    internal const val CASHBLOCK_CONNECT_ROULETTE = "com.avatye.cashblock.feature.roulette.block.Connector"
    internal const val CASHBLOCK_CONNECT_OFFERWALL = "com.avatye.cashblock.feature.offerwall.block.Connector"
    internal const val CASHBLOCK_CONNECT_PUBLISHER = "com.avatye.cashblock.publisher.Connector"
    // endregion

    // logger
    internal val logger: LogHandler = LogHandler(moduleName = MODULE_NAME)

    // region # core base config
    internal lateinit var application: Application
        private set

    internal lateinit var coreBlockCode: BlockCode
        private set

    internal val isInitialized: Boolean
        get() {
            return FeatureCore::application.isInitialized
                    && FeatureCore::coreBlockCode.isInitialized
        }
    // endregion


    // region # environment
    internal val allowLog: Boolean by lazy {
        if (FeatureCore::application.isInitialized) {
            val logFlag = application.metaDataValue(CASHBLOCK_CONFIG_LOG) ?: "false"
            logFlag.equals(other = "true", ignoreCase = true)
        } else {
            false
        }
    }

    internal val allowDeveloper: Boolean by lazy {
        if (FeatureCore::application.isInitialized) {
            val flag = application.metaDataValue(CASHBLOCK_CONFIG_DEVELOPER) ?: "false"
            flag.equals(other = "true", ignoreCase = true)
        } else {
            false
        }
    }

    internal val appEnvironment: ServeEnvironment by lazy {
        if (FeatureCore::application.isInitialized) {
            ServeEnvironment.from(value = application.metaDataValue(CASHBLOCK_CONFIG_ENVIRONMENT) ?: "live")
        } else {
            ServeEnvironment.LIVE
        }
    }
    // endregion


    // region # config
    internal const val appServiceName: String = "CashBlock"

    internal val appVersionCode: String by lazy {
        if (FeatureCore::application.isInitialized) application.hostAppVersionCode else ""
    }

    internal val appVersionName: String by lazy {
        if (FeatureCore::application.isInitialized) application.hostAppVersionName else ""
    }

    internal val appName: String by lazy {
        if (FeatureCore::application.isInitialized) application.hostAppName else ""
    }

    internal val appPackageName: String by lazy {
        if (FeatureCore::application.isInitialized) application.hostPackageName else ""
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
        // app-id & secret
        this.application.metaDataValue(CASHBLOCK_KEY_ROULETTE).let {
            if (it.isNullOrEmpty()) {
                throw RuntimeException("$CASHBLOCK_KEY_ROULETTE is null or empty")
            }
            this.coreBlockCode = BlockCode.create(blockType = BlockType.ROULETTE, appKey = it)
        }
        // channel talk
        ChannelTalkUtil.init(application = application)
    }


    internal fun initialize(application: Application, blockCode: BlockCode) {
        this.application = application
        this.coreBlockCode = blockCode
        // channel talk
        ChannelTalkUtil.init(application = application)
    }


    internal fun setAppUserId(appUserId: String) = LoginController.setLoginUserId(appUserId = appUserId)


    internal fun getAppUserId() = LoginController.getLoginUserId()
}