package com.avatye.cashblock.base.component.domain.entity.ad

import com.avatye.cashblock.base.library.ad.aaid.AdvertiseIDTask

data class AAIDEntity(
    val isLimitAdTrackingEnabled: Boolean,
    val aaid: String,
    val needUpdate: Boolean
) {
    val isValid: Boolean
        get() {
            return AdvertiseIDTask.isValid(this.aaid)
        }
}