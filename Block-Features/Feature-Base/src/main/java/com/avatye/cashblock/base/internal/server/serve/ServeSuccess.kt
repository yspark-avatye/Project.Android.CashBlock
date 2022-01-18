package com.avatye.cashblock.base.internal.server.serve

internal abstract class ServeSuccess {

    private var rawValue: String? = null

    fun setResponseRawValue(responseValue: String) {
        this.rawValue = responseValue
        this.makeBody(responseValue)
    }

    @Throws(Exception::class)
    protected abstract fun makeBody(responseValue: String)

    override fun toString(): String {
        return if (rawValue.isNullOrEmpty()) {
            ""
        } else {
            rawValue!!
        }
    }

    val rawString: String get() = rawValue ?: ""

}