package com.avatye.cashblock.base.internal.server.serve

enum class ServeEnvironment(val value: String) {
    DEV("dev"),
    TEST("test"),
    STAGE("stage"),
    QA("qa"),
    LIVE("live");

    companion object {
        fun from(value: String): ServeEnvironment {
            return when (value.lowercase()) {
                "dev" -> DEV
                "test" -> TEST
                "stage" -> STAGE
                "qa" -> QA
                "live" -> LIVE
                else -> LIVE
            }
        }
    }
}