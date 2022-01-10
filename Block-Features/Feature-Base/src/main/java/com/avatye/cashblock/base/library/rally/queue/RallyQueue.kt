package com.avatye.cashblock.base.library.rally.queue

import com.android.volley.Response

data class RallyQueue(
    val reqTag: String? = null,
    val reqMethod: Int,
    val reqUrl: String,
    val reqHeader: MutableMap<String, String>? = null,
    val reqBody: ByteArray? = null,
    val resListener: Response.Listener<RallyQueueResult>,
    val errorListener: Response.ErrorListener
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RallyQueue

        if (reqTag != other.reqTag) return false
        if (reqMethod != other.reqMethod) return false
        if (reqUrl != other.reqUrl) return false
        if (reqHeader != other.reqHeader) return false
        if (reqBody != null) {
            if (other.reqBody == null) return false
            if (!reqBody.contentEquals(other.reqBody)) return false
        } else if (other.reqBody != null) return false
        if (resListener != other.resListener) return false
        if (errorListener != other.errorListener) return false

        return true
    }

    override fun hashCode(): Int {
        var result = reqTag?.hashCode() ?: 0
        result = 31 * result + reqMethod.hashCode()
        result = 31 * result + reqUrl.hashCode()
        result = 31 * result + (reqHeader?.hashCode() ?: 0)
        result = 31 * result + (reqBody?.contentHashCode() ?: 0)
        result = 31 * result + resListener.hashCode()
        result = 31 * result + errorListener.hashCode()
        return result
    }

}