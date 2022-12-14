package com.adadapted.android.sdk.ext.http

import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.intercept.InterceptAdapter
import com.adadapted.android.sdk.core.intercept.InterceptEvent
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.ext.json.JsonInterceptBuilder
import com.adadapted.android.sdk.ext.json.JsonInterceptEventBuilder
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest

class HttpInterceptAdapter(private val initUrl: String, private val eventUrl: String, private val httpQueueManager: HttpQueueManager = HttpRequestManager) : InterceptAdapter {
    private val LOGTAG = HttpPayloadAdapter::class.java.name
    private val kiBuilder: JsonInterceptBuilder = JsonInterceptBuilder()
    private val eventBuilder: JsonInterceptEventBuilder = JsonInterceptEventBuilder()

    override fun retrieve(session: Session, callback: InterceptAdapter.Callback) {
        if (session.id.isEmpty()) {
            return
        }
        val url = initUrl + String.format("?aid=%s", session.getDeviceInfo().appId) + String.format("&uid=%s", session.getDeviceInfo().udid) + String.format("&sid=%s", session.id) + String.format("&sdk=%s", session.getDeviceInfo().sdkVersion)
        val jsonRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                callback.onSuccess(kiBuilder.build(response))
            },
            { error ->
                HttpErrorTracker.trackHttpError(
                    error,
                    initUrl,
                    EventStrings.KI_SESSION_REQUEST_FAILED,
                    LOGTAG
                )
            })
        httpQueueManager.queueRequest(jsonRequest)
    }

    override fun sendEvents(session: Session, events: MutableSet<InterceptEvent>) {
        val json = eventBuilder.marshalEvents(session, events)
        val jsonRequest = JsonObjectRequest(
            Request.Method.POST,
            eventUrl,
            json,
            {},
            { error ->
                HttpErrorTracker.trackHttpError(
                    error,
                    eventUrl,
                    EventStrings.KI_EVENT_REQUEST_FAILED,
                    LOGTAG
                )
            })
        httpQueueManager.queueRequest(jsonRequest)
    }
}