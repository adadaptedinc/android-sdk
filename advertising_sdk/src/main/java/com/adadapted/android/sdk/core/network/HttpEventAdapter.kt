package com.adadapted.android.sdk.core.network

import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.event.AdEvent
import com.adadapted.android.sdk.core.event.EventAdapter
import com.adadapted.android.sdk.core.event.EventRequestBuilder
import com.adadapted.android.sdk.core.event.SdkError
import com.adadapted.android.sdk.core.event.SdkEvent
import com.adadapted.android.sdk.core.log.AALogger
import com.adadapted.android.sdk.core.network.HttpConnector.API_HEADER
import com.adadapted.android.sdk.core.session.Session
import io.ktor.client.request.*
import io.ktor.http.*

class HttpEventAdapter(private val adEventUrl: String, private val sdkEventUrl: String, private val errorUrl: String, private val httpConnector: HttpConnector) :
    EventAdapter {
    override suspend fun publishAdEvents(appId: String, adEvents: Set<AdEvent>) {
        try {
            httpConnector.client.post(adEventUrl) {
                contentType(ContentType.Application.Json)
                setBody(EventRequestBuilder.buildAdEventRequest(session, adEvents))
                header(API_HEADER, session.deviceInfo.appId)
            }
        } catch (e: Exception) {
            e.message?.let { AALogger.logError(it) }
            HttpErrorTracker.trackHttpError(
                e.cause.toString(),
                e.message.toString(),
                EventStrings.AD_EVENT_TRACK_REQUEST_FAILED,
                adEventUrl
            )
        }
    }

    override suspend fun publishSdkEvents(appId: String, events: Set<SdkEvent>) {
        try {
            httpConnector.client.post(sdkEventUrl) {
                contentType(ContentType.Application.Json)
                setBody(EventRequestBuilder.buildEventRequest(session, sdkEvents = events))
                header(API_HEADER, session.deviceInfo.appId)
            }
        } catch (e: Exception) {
            e.message?.let { AALogger.logError(it) }
            HttpErrorTracker.trackHttpError(
                e.cause.toString(),
                e.message.toString(),
                EventStrings.SDK_EVENT_REQUEST_FAILED,
                adEventUrl
            )
        }
    }

    override suspend fun publishSdkErrors(appId: String, errors: Set<SdkError>) {
        try {
            httpConnector.client.post(errorUrl) {
                contentType(ContentType.Application.Json)
                setBody(EventRequestBuilder.buildEventRequest(session, sdkErrors = errors))
                header(API_HEADER, session.deviceInfo.appId)
            }
        } catch (e: Exception) {
            AALogger.logError("SDK Error Request Failed -> " + e.message)
        }
    }
}