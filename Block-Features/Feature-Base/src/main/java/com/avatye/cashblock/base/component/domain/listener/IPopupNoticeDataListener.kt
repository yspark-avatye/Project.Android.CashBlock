package com.avatye.cashblock.base.component.domain.listener

interface IPopupNoticeDataListener {
    fun getItems(): LinkedHashMap<String, Int> = LinkedHashMap()
    fun setItems(data: Map<String, Int>): Unit
}