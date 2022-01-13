package com.avatye.cashblock.base.internal.server.serve

internal interface ServeResponse<in T : ServeSuccess> {
    fun onSuccess(success: T)
    fun onFailure(failure: ServeFailure)
}