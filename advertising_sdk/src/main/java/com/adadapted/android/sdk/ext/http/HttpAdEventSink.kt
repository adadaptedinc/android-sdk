package com.adadapted.android.sdk.ext.http

import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.ad.AdEvent
import com.adadapted.android.sdk.core.ad.AdEventSink
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.ext.json.AdAdaptedJsonObjectRequest
import com.adadapted.android.sdk.ext.json.JsonAdEventBuilder
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest

class HttpAdEventSink(private val batchUrl: String, private val httpQueueManager: HttpQueueManager = HttpRequestManager) : AdEventSink {
    private val LOGTAG = HttpAdEventSink::class.java.name
    private val builder: JsonAdEventBuilder = JsonAdEventBuilder()

    override fun sendBatch(session: Session, events: Set<AdEvent>) {
        val json = builder.marshalEvents(session, events)

        val jsonRequest = AdAdaptedJsonObjectRequest(
            httpQueueManager.getAppId(),
            Request.Method.POST,
            batchUrl,
            json,
            {},
            { error ->
                HttpErrorTracker.trackHttpError(
                    error,
                    batchUrl,
                    EventStrings.AD_EVENT_TRACK_REQUEST_FAILED,
                    LOGTAG
                )
            })
        httpQueueManager.queueRequest(jsonRequest)
    }
}