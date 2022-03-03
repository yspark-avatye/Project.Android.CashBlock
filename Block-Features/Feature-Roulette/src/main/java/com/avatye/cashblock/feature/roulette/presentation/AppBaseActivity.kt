package com.avatye.cashblock.feature.roulette.presentation

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import com.avatye.cashblock.base.block.BlockType
import com.avatye.cashblock.base.component.domain.model.app.CoreBaseActivity
import com.avatye.cashblock.feature.roulette.RouletteConfig


internal abstract class AppBaseActivity : CoreBaseActivity() {

    override fun getBlockType(): BlockType = RouletteConfig.blockType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 23) {
            window.statusBarColor = Color.parseColor("#99CCFF")
            val isWindowLightStatusBar = false
            when (isWindowLightStatusBar) {
                true -> window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                false -> window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }
}