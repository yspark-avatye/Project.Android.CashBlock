package com.avatye.cashblock.feature.roulette.component.data

internal object AppConstData {
    object Roulette {
        const val rotate = (360 * 10).toFloat()
        const val angleWin = 90F
        val angleLose = floatArrayOf(0F, 45F, 135F, 180F, 225F, 270F, 315F)
    }
}