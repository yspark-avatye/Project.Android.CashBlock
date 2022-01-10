package com.avatye.cashblock.base.component.domain.entity.support

data class PopupNoticeEntity(
    val popupID: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val landingName: String = "",
    val landingValue: String = "",
    val displayType: PopupDisplayType = PopupDisplayType.ONETIME
) {
    val enableLanding: Boolean get() = landingName.isNotEmpty()
}
