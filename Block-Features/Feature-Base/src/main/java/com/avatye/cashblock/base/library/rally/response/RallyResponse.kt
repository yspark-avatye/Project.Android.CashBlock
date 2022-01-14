package com.avatye.cashblock.base.library.rally.response

internal interface RallyResponse {
    fun onSuccess(response: RallySuccess)
    fun onFailure(error: RallyFailure)
}