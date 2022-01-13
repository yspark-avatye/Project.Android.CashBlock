package com.avatye.cashblock.base.component.domain.listener

interface ILoginListener {
    fun onSuccess()
    fun onFailure(reason: String)
}