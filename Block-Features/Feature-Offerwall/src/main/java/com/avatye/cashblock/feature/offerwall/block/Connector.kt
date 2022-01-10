package com.avatye.cashblock.feature.offerwall.block

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.annotation.Keep
import com.avatye.cashblock.base.block.BlockCode
import com.avatye.cashblock.base.block.BlockConnector
import com.avatye.cashblock.base.component.domain.entity.base.LandingType
import com.avatye.cashblock.feature.offerwall.BuildConfig
import com.avatye.cashblock.feature.offerwall.CashBlockOfferwall
import com.avatye.cashblock.feature.offerwall.OfferwallConfig.logger

@Keep
class Connector : BlockConnector() {
    override val blockName: String = BuildConfig.X_BUILD_SDK_NAME
    override val blockVersion: Int = BuildConfig.X_BUILD_SDK_VERSION_CODE
    override val blockVersionName: String = BuildConfig.X_BUILD_SDK_VERSION_NAME

    override fun initialize(application: Application, blockCode: BlockCode) {
        logger.i { "$blockName -> Connector::initialize $blockCode" }
    }

    override fun launch(context: Context, blockCode: BlockCode?) {
        logger.i { "$blockName -> Connector::launch(${blockCode})" }
        CashBlockOfferwall.openFromConnector(context = context, blockCode = blockCode)
    }

    override fun clearSession(context: Context) {
        logger.i { "$blockName -> Connector::clearSession()" }
    }

    override fun landing(ownerActivity: Activity, ownerActivityClose: Boolean, landingType: LandingType, landingValue: String?) {
    }
}