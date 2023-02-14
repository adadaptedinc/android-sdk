package com.adadapted.android.sdk.tools

import android.content.Context
import com.adadapted.android.sdk.ext.http.HttpQueueManager
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.VolleyError

class TestHttpRequestManager: HttpQueueManager {
    var shouldReturnError = false
    var queueWasCreated: Boolean = false
    var queuedRequest: Request<*>? = null

    fun reset() {
        shouldReturnError = false
        queueWasCreated = false
        queuedRequest = null
    }

    override fun createQueue(context: Context, apiKey: String) {
        queueWasCreated = true
    }

    override fun queueRequest(request: Request<*>) {
        queuedRequest = request
        if (shouldReturnError) {
            request.errorListener.onErrorResponse(VolleyError(NetworkResponse(404, ByteArray(0), true, 5, mutableListOf())))
        }
    }

    override fun getAppId(): String {
        return "TESTAPIKEY"
    }
}