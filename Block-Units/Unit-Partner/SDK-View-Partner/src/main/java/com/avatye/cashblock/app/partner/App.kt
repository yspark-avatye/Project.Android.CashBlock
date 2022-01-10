package com.avatye.cashblock.app.partner

import android.app.Application
import com.avatye.cashblock.CashBlockSDK

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        CashBlockSDK.initialize(application = this)
    }
}