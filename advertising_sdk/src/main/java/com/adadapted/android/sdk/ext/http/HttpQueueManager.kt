package com.adadapted.android.sdk.ext.http

import android.content.Context
import com.android.volley.Request

interface HttpQueueManager {
    fun createQueue(context: Context)
    fun queueRequest(request: Request<*>)
}