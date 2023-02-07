package com.adadapted.android.sdk.ext.http

import android.content.Context
import com.android.volley.Request

interface HttpQueueManager {
    fun createQueue(context: Context, apiKey: String)
    fun queueRequest(request: Request<*>)
    fun getAppId(): String
}