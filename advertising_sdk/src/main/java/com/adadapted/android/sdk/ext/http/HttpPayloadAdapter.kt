package com.adadapted.android.sdk.ext.http

import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.addit.PayloadAdapter
import com.adadapted.android.sdk.core.addit.PayloadContentParser
import com.adadapted.android.sdk.core.addit.PayloadEvent
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.ext.json.AdAdaptedJsonObjectRequest
import com.adadapted.android.sdk.ext.json.JsonPayloadBuilder
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest

class HttpPayloadAdapter(private val pickupUrl: String, private val trackUrl: String, private val httpQueueManager: HttpQueueManager = HttpRequestManager) : PayloadAdapter {
    private val LOGTAG = HttpPayloadAdapter::class.java.name
    private val builder: JsonPayloadBuilder = JsonPayloadBuilder()
    private val parser: PayloadContentParser = PayloadContentParser()

    override fun pickup(deviceInfo: DeviceInfo, callback: PayloadAdapter.Callback) {
        val json = builder.buildRequest(deviceInfo)
        val request = AdAdaptedJsonObjectRequest(
            httpQueueManager.getAppId(),
            Request.Method.POST,
            pickupUrl,
            json,
            { response ->
                val content = parser.parse(response)
                callback.onSuccess(content)
            },
            { error ->
                HttpErrorTracker.trackHttpError(
                    error,
                    pickupUrl,
                    EventStrings.PAYLOAD_PICKUP_REQUEST_FAILED,
                    LOGTAG
                )
            })
        httpQueueManager.queueRequest(request)
    }

    override fun publishEvent(event: PayloadEvent) {
        val json = builder.buildEvent(event)
        val request = AdAdaptedJsonObjectRequest(
            httpQueueManager.getAppId(),
            Request.Method.POST,
            trackUrl,
            json,
            { },
            { error ->
                HttpErrorTracker.trackHttpError(
                    error,
                    trackUrl,
                    EventStrings.PAYLOAD_EVENT_REQUEST_FAILED,
                    LOGTAG
                )
            })
        httpQueueManager.queueRequest(request)
    }
}