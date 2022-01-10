package com.avatye.cashblock.base.library.ad.curator.queue

import com.avatye.cashblock.base.library.ad.curator.queue.loader.ADLoaderType

data class CuratorQueueEntity(val loaderType: ADLoaderType, val placementID: String)