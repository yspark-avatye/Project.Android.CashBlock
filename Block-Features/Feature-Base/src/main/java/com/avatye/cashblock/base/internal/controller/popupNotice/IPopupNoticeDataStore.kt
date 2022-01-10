package com.avatye.cashblock.base.internal.controller.popupNotice

interface IPopupNoticeDataStore {
    fun getItems(): LinkedHashMap<String, Int> = LinkedHashMap()
    fun setItems(data: Map<String, Int>): Unit
}