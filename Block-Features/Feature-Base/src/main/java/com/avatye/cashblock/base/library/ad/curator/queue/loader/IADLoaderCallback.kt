package com.avatye.cashblock.base.library.ad.curator.queue.loader

internal interface IADLoaderCallback {
    fun onLoaded()
    fun ondFailed(isBlocked: Boolean)
    fun onOpened()
    fun onClosed(isCompleted: Boolean)
}