package com.adadapted.android.sdk.core.network

import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.atl.AddItContent
import com.adadapted.android.sdk.core.atl.AddItContentParser
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.log.AALogger
import com.adadapted.android.sdk.core.network.HttpConnector.API_HEADER
import com.adadapted.android.sdk.core.payload.PayloadAdapter
import com.adadapted.android.sdk.core.payload.PayloadEvent
import com.adadapted.android.sdk.core.payload.PayloadRequestBuilder
import com.adadapted.android.sdk.core.payload.PayloadResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class HttpPayloadAdapter(
    private val pickupUrl: String,
    private val trackUrl: String,
    private val httpConnector: HttpConnector
) : PayloadAdapter {
    override suspend fun pickup(deviceInfo: DeviceInfo, callback: (content: List<AddItContent>) -> Unit) {
        try {
            val response: HttpResponse = httpConnector.client.post(pickupUrl) {
                contentType(ContentType.Application.Json)
                setBody(PayloadRequestBuilder.buildRequest(deviceInfo))
                header(API_HEADER, deviceInfo.appId)
            }
            response.body<PayloadResponse>().apply { AddItContentParser.generateAddItContentFromPayloads(this).apply(callback) }
        } catch (e: Exception) {
            e.message?.let { AALogger.logError(it) }
            HttpErrorTracker.trackHttpError(
                e.cause.toString(),
                e.message.toString(),
                EventStrings.PAYLOAD_PICKUP_REQUEST_FAILED,
                pickupUrl
            )
        }
    }

    override suspend fun publishEvent(deviceInfo: DeviceInfo, event: PayloadEvent) {
        try {
            httpConnector.client.post(trackUrl) {
                contentType(ContentType.Application.Json)
                setBody(PayloadRequestBuilder.buildEventRequest(deviceInfo, event))
                header(API_HEADER, deviceInfo.appId)
            }
        } catch (e: Exception) {
            e.message?.let { AALogger.logError(it) }
            HttpErrorTracker.trackHttpError(
                e.cause.toString(),
                e.message.toString(),
                EventStrings.PAYLOAD_EVENT_REQUEST_FAILED,
                trackUrl
            )
        }
    }
}