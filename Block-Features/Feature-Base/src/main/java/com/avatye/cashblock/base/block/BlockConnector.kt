package com.avatye.cashblock.base.block

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.annotation.Keep
import com.avatye.cashblock.base.component.domain.entity.base.LandingType

@Keep
abstract class BlockConnector {
    abstract val blockName: String
    abstract val blockVersion: Int
    abstract val blockVersionName: String
    abstract fun initialize(application: Application)
    abstract fun clearSession(context: Context)
    abstract fun launch(context: Context)
    abstract fun landing(ownerActivity: Activity, ownerActivityClose: Boolean = false, landingType: LandingType, landingValue: String? = null)
}