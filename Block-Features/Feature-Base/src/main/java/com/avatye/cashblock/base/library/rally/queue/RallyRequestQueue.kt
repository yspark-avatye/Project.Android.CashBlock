package com.avatye.cashblock.base.library.rally.queue

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import java.io.UnsupportedEncodingException

class RallyRequestQueue(private val queue: RallyQueue) : Request<RallyQueueResult>(
    queue.reqMethod,
    queue.reqUrl,
    queue.errorListener
) {
    private val _lock = Any()
    private var _responseListener: Response.Listener<RallyQueueResult>? = null

    override fun cancel() {
        super.cancel()
        synchronized(_lock) { _responseListener = null }
    }

    init {
        this.tag = queue.reqTag
        this._responseListener = queue.resListener
    }

    override fun getHeaders(): MutableMap<String, String> = queue.reqHeader ?: super.getHeaders()

    override fun getBody(): ByteArray? = queue.reqBody ?: super.getBody()

    override fun getBodyContentType(): String = "application/json; charset=utf-8"

    override fun deliverResponse(response: RallyQueueResult) {
        var listener: Response.Listener<RallyQueueResult>?
        // lock
        synchronized(_lock) {
            listener = _responseListener
        }
        listener?.onResponse(response)
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<RallyQueueResult> {
        val queueResult = RallyQueueResult(
            statusCode = response.statusCode,
            header = response.headers as? HashMap<String, String>,
            body = try {
                String(response.data, Charsets.UTF_8)
            } catch (e: UnsupportedEncodingException) {
                ""
            }
        )
        return Response.success(queueResult, HttpHeaderParser.parseCacheHeaders(response))
    }
}