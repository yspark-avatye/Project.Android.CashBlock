package com.avatye.cashblock.base.internal.server.entity

import com.avatye.cashblock.base.internal.server.serve.ServeSuccess


class ResVoid : ServeSuccess() {
    override fun makeBody(responseValue: String) {
        // NOPE
    }
}