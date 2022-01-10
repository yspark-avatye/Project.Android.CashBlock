package com.avatye.cashblock.base.library.ad.curator.queue

import com.avatye.cashblock.base.library.ad.curator.queue.loader.ADLoaderBase

interface ICuratorQueueCallback {
    fun onLoaded(loader: ADLoaderBase)
    fun onOpened()
    fun onComplete(success: Boolean)
    fun ondFailed(isBlocked: Boolean)
    fun onNeedAgeVerification()
}