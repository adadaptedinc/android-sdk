package com.adadapted.android.sdk.core.network

import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.keyword.InterceptAdapter
import com.adadapted.android.sdk.core.keyword.InterceptEvent
import com.adadapted.android.sdk.core.keyword.InterceptEventWrapper
import com.adadapted.android.sdk.core.keyword.KeywordRequest
import com.adadapted.android.sdk.core.keyword.KeywordResponse
import com.adadapted.android.sdk.core.log.AALogger
import com.adadapted.android.sdk.core.network.HttpConnector.API_HEADER
import com.adadapted.android.sdk.core.network.HttpConnector.decompressAndDeserialize
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class HttpInterceptAdapter(private val keywordRequestUrl: String, private val eventUrl: String, private val httpConnector: HttpConnector) :
    InterceptAdapter {
    override suspend fun retrieve(sessionId: String, listener: InterceptAdapter.Listener) {
        val deviceInfo = DeviceInfoClient.getCachedDeviceInfo()
        try {
            val keywordRequest = KeywordRequest(
                sdkId = deviceInfo.sdkVersion,
                bundleId = "",
                userId = deviceInfo.udid,
                zoneId = "",
                sessionId = sessionId,
                extra = ""
            )

            val response: HttpResponse = httpConnector.client.post(keywordRequestUrl) {
                contentType(ContentType.Application.Json)
                setBody(keywordRequest)
                header(API_HEADER, deviceInfo.appId)
            }

            val keywordResponse: KeywordResponse = response.decompressAndDeserialize()
            keywordResponse.data?.let { listener.onSuccess(it) }
        } catch (e: Exception) {
            e.message?.let { AALogger.logError(it) }
            HttpErrorTracker.trackHttpError(
                e.cause.toString(),
                e.message.toString(),
                EventStrings.KI_INIT_REQUEST_FAILED,
                keywordRequestUrl
            )
        }
    }

    override suspend fun sendEvents(sessionId: String, events: MutableSet<InterceptEvent>) {
        val deviceInfo = DeviceInfoClient.getCachedDeviceInfo()
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