package com.adadapted.android.sdk.core.network

import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.keyword.InterceptAdapter
import com.adadapted.android.sdk.core.keyword.InterceptEvent
import com.adadapted.android.sdk.core.keyword.InterceptEventWrapper
import com.adadapted.android.sdk.core.log.AALogger
import com.adadapted.android.sdk.core.network.HttpConnector.API_HEADER
import com.adadapted.android.sdk.core.session.Session
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class HttpInterceptAdapter(private val initUrl: String, private val eventUrl: String, private val httpConnector: HttpConnector) :
    InterceptAdapter {
    override suspend fun retrieve(session: Session, listener: InterceptAdapter.Listener) {
        if (session.id.isEmpty()) {
            return
        }
        try {
            val url = initUrl + "?aid=" + session.deviceInfo.appId + "&uid=" + session.deviceInfo.udid + "&sid=" + session.id + "&sdk=" + session.deviceInfo.sdkVersion
            val response: HttpResponse = httpConnector.client.get(url) {
                contentType(ContentType.Application.Json)
                header(API_HEADER, session.deviceInfo.appId)
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

    override suspend fun sendEvents(session: Session, events: MutableSet<InterceptEvent>) {
        val compiledInterceptEventRequest = InterceptEventWrapper(
            session.id,
            session.deviceInfo.appId,
            session.deviceInfo.udid,
            session.deviceInfo.sdkVersion,
            events
        )
        try {
            httpConnector.client.post(eventUrl) {
                contentType(ContentType.Application.Json)
                setBody(compiledInterceptEventRequest)
                header(API_HEADER, session.deviceInfo.appId)
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