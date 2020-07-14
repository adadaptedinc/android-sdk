package com.adadapted.android.sdk.ext.http

import android.util.Log
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.event.AppError
import com.adadapted.android.sdk.core.event.AppEvent
import com.adadapted.android.sdk.core.event.AppEventSink
import com.adadapted.android.sdk.ext.json.JsonAppEventBuilder
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject

class HttpAppEventSink(private val eventUrl: String, private val errorUrl: String, private val httpQueueManager: HttpQueueManager = HttpRequestManager) : AppEventSink {
    private val eventBuilder: JsonAppEventBuilder = JsonAppEventBuilder()
    private var eventWrapper: JSONObject? = null
    private var errorWrapper: JSONObject? = null

    override fun generateWrappers(deviceInfo: DeviceInfo) {
        eventWrapper = eventBuilder.buildWrapper(deviceInfo)
        errorWrapper = eventBuilder.buildWrapper(deviceInfo)
    }

    override fun publishEvent(events: Set<AppEvent>) {
        if (eventWrapper == null) {
            Log.w(LOGTAG, "No event wrapper")
            return
        }
        val json = eventBuilder.buildEventItem(eventWrapper, events)
        val jsonRequest = JsonObjectRequest(
                Request.Method.POST,
                eventUrl,
                json,
                Response.Listener { },
                Response.ErrorListener { error ->
                    HttpErrorTracker.trackHttpError(error, eventUrl, EventStrings.APP_EVENT_REQUEST_FAILED, LOGTAG)
                })
        httpQueueManager.queueRequest(jsonRequest)
    }

    override fun publishError(errors: Set<AppError>) {
        if (errorWrapper == null) {
            Log.w(LOGTAG, "No error wrapper")
            return
        }
        val json = eventBuilder.buildErrorItem(errorWrapper, errors)
        val jsonRequest = JsonObjectRequest(
                Request.Method.POST,
                errorUrl,
                json,
                Response.Listener { },
                Response.ErrorListener { error ->
                    if (error?.networkResponse != null) {
                        val statusCode = error.networkResponse.statusCode
                        val data = String(error.networkResponse.data)
                        Log.e(LOGTAG, "App Error Request Failed: $statusCode - $data", error)
                    }
                })
        httpQueueManager.queueRequest(jsonRequest)
    }

    companion object {
        private val LOGTAG = HttpAppEventSink::class.java.name
    }
}