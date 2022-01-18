package com.avatye.cashblock.base.library.rally

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.avatye.cashblock.base.Core.logger
import com.avatye.cashblock.base.library.miscellaneous.SingletonContextHolder
import com.avatye.cashblock.base.library.rally.queue.RallyRequestQueue
import com.avatye.cashblock.base.library.rally.request.RallyRequest
import com.avatye.cashblock.base.library.rally.response.RallyResponse

internal class Rally private constructor(private val context: Context) {

    companion object : SingletonContextHolder<Rally>(::Rally) {
        fun addToRequest(context: Context, request: RallyRequest, response: RallyResponse) {
            RallyTask(context = context, request = request, response = response).also {
                Rally.create(context = context).addToRequestQueue(it.createRallyRequestQueue())
            }
        }

        fun cancelToRequestQueue(context: Context, tag: String) {
            Rally.create(context = context).cancelToRequestQueue(tag)
        }

        fun cancelToRequestAllQueue(context: Context) {
            Rally.create(context = context).cancelToRequestAllQueue()
        }
    }

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    private fun addToRequestQueue(request: RallyRequestQueue) {
        requestQueue.add(request)
        logger.i(viewName = "Rally") { "addToRequestQueue { requestTag: ${request.tag} }" }
    }

    private fun cancelToRequestQueue(tag: String) {
        requestQueue.cancelAll(tag)
        logger.i(viewName = "Rally") { "cancelToRequestQueue { tag: $tag }" }
    }

    private fun cancelToRequestAllQueue() {
        requestQueue.cancelAll { req ->
            logger.i(viewName = "Rally") { "cancelToRequestAllQueue { tag: ${req.tag} }" }
            true
        }
    }
}