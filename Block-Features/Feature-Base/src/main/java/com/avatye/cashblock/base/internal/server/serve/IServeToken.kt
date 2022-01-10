package com.avatye.cashblock.base.internal.server.serve

interface IServeToken {
    fun makeBasicToken(): String
    fun makeBearerToken(): String
}