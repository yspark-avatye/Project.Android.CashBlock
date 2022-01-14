package com.avatye.cashblock.feature.offerwall.block

import android.app.Activity
import android.content.Context
import androidx.annotation.Keep
import com.avatye.cashblock.base.block.BlockConnector
import com.avatye.cashblock.base.component.domain.entity.base.LandingType
import com.avatye.cashblock.feature.offerwall.BuildConfig
import com.avatye.cashblock.feature.offerwall.OfferwallConfig.logger
import com.avatye.cashblock.feature.offerwall.component.controller.EntryController

@Keep
internal class Connector : BlockConnector() {
    override val blockName: String = BuildConfig.X_BUILD_SDK_NAME
    override val blockVersion: Int = BuildConfig.X_BUILD_SDK_VERSION_CODE
    override val blockVersionName: String = BuildConfig.X_BUILD_SDK_VERSION_NAME

    override fun connect(context: Context) {
        logger.i(viewName = blockName) { "Connector::launch" }
        EntryController.connect(context = context)
    }

    override fun clearSession(context: Context) {
        logger.i(viewName = blockName) { "Connector::clearSession" }
    }

    override fun landing(ownerActivity: Activity, ownerActivityClose: Boolean, landingType: LandingType, landingValue: String?) {
        logger.i(viewName = blockName) { "Connector::landing" }
    }
}