package com.avatye.cashblock.feature.roulette.component.model.listener

interface IUpdateNotification {
    fun onTicketChanged()
    fun onStatusChanged(isActive: Boolean)
}