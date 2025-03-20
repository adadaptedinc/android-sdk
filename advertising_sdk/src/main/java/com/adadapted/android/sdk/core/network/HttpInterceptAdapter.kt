package com.adadapted.android.sdk.core.network

import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.keyword.InterceptAdapter
import com.adadapted.android.sdk.core.keyword.InterceptEvent
import com.adadapted.android.sdk.core.keyword.InterceptEventWrapper
import com.adadapted.android.sdk.core.log.AALogger
import com.adadapted.android.sdk.core.network.HttpConnector.API_HEADER
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class HttpInterceptAdapter(private val initUrl: String, private val eventUrl: String, private val httpConnector: HttpConnector) :
    InterceptAdapter {
    override suspend fun retrieve(sessionId: String, listener: InterceptAdapter.Listener) {
        var deviceInfo = DeviceInfoClient.getCachedDeviceInfo()
        try {
            val url = initUrl + "?aid=" + deviceInfo.appId + "&uid=" + deviceInfo.udid + "&sid=" + sessionId + "&sdk=" + deviceInfo.sdkVersion
            val response: HttpResponse = httpConnector.client.get(url) {
                contentType(ContentType.Application.Json)
                header(API_HEADER, deviceInfo.appId)
            }
            listener.onSuccess(response.body())
        } catch (e: Exception) {
            e.message?.let { AALogger.logError(it) }
            HttpErrorTracker.trackHttpError(
                e.cause.toString(),
                e.message.toString(),
                EventStrings.KI_INIT_REQUEST_FAILED,
                initUrl
            )
        }
    }

    override suspend fun sendEvents(sessionId: String, events: MutableSet<InterceptEvent>) {
        var deviceInfo = DeviceInfoClient.getCachedDeviceInfo()
        val compiledInterceptEventRequest = InterceptEventWrapper(
            sessionId,
            deviceInfo.appId,
            deviceInfo.udid,
            deviceInfo.sdkVersion,
            events
        )
        try {
            httpConnector.client.post(eventUrl) {
                contentType(ContentType.Application.Json)
                setBody(compiledInterceptEventRequest)
                header(API_HEADER, deviceInfo.appId)
            }
        } catch (e: Exception) {
            e.message?.let { AALogger.logError(it) }
            HttpErrorTracker.trackHttpError(
                e.cause.toString(),
                e.message.toString(),
                EventStrings.KI_EVENT_REQUEST_FAILED,
                eventUrl
            )
        }
    }
}