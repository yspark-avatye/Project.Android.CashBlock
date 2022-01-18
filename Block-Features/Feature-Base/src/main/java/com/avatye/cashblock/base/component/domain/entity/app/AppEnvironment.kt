package com.avatye.cashblock.base.component.domain.entity.app

enum class AppEnvironment(val value: String) {
    DEV("dev"),
    TEST("test"),
    STAGE("stage"),
    QA("qa"),
    LIVE("live");

    companion object {
        fun from(value: String): AppEnvironment {
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