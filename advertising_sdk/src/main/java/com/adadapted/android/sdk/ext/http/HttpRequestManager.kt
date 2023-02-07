package com.adadapted.android.sdk.ext.http

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

object HttpRequestManager : HttpQueueManager {
    private val LOGTAG = HttpRequestManager::class.java.name
    private lateinit var requestQueue: RequestQueue
    private lateinit var appId: String

    override fun createQueue(context: Context, apiKey: String) {
        requestQueue = Volley.newRequestQueue(context.applicationContext)
        appId = apiKey
    }

    override fun getAppId(): String {
        return appId
    }

    @Synchronized
    override fun queueRequest(request: Request<*>) {
        if (this::requestQueue.isInitialized) {
            requestQueue.add(request)
        } else {
            Log.e(LOGTAG, "HTTP Request Queue has not been initialized.")
        }
    }
}