package com.avatye.cashblock.publisher

import com.avatye.cashblock.base.library.LogHandler

internal const val MODULE_NAME = "Publisher"

internal object PublisherConfig {
    // logger
    val logger: LogHandler = LogHandler(moduleName = MODULE_NAME)
}