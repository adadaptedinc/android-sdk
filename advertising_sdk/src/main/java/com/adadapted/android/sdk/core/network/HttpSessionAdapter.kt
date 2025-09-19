package com.adadapted.android.sdk.core.network

import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.interfaces.AdGetListener
import com.adadapted.android.sdk.core.interfaces.SessionAdapter
import com.adadapted.android.sdk.core.interfaces.SessionInitListener
import com.adadapted.android.sdk.core.log.AALogger
import com.adadapted.android.sdk.core.network.HttpConnector.API_HEADER
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.view.ZoneContext
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

class HttpSessionAdapter(
    private val initUrl: String,
    private val refreshUrl: String,
    private val httpConnector: HttpConnector
) : SessionAdapter {
    override suspend fun sendInit(
        deviceInfo: DeviceInfo,
        listener: SessionInitListener
    ) {
        try {
            val response: HttpResponse = httpConnector.client.post(initUrl) {
                contentType(ContentType.Application.Json)
                setBody(deviceInfo)
                header(API_HEADER, deviceInfo.appId)
            }

            listener.onSessionInitialized(response.body<Session>().apply { this.deviceInfo = deviceInfo })

        } catch (e: Exception) {
            e.message?.let { AALogger.logError(it) }
            HttpErrorTracker.trackHttpError(
                e.cause.toString(),
                e.message.toString(),
                EventStrings.SESSION_REQUEST_FAILED,
                initUrl
            )
            listener.onSessionInitializeFailed()
        }
    }

    override suspend fun sendRefreshAds(
        session: Session,
        listener: AdGetListener,
        zoneContexts: MutableSet<ZoneContext>
    ) {
        try {
            val zoneIdsInContext = zoneContexts.joinToString(",") { it.zoneId }
            val zoneContextId = zoneContexts.firstOrNull()?.contextId ?: ""
            val url = refreshUrl + ("?aid=" + session.deviceInfo.appId +
                    "&uid=" + session.deviceInfo.udid +
                    "&sid=" + session.id +
                    "&sdk=" + session.deviceInfo.sdkVersion +
                    "&zoneID=" + zoneIdsInContext +
                    "&contextID=" + zoneContextId)
            val response: HttpResponse = httpConnector.client.get(url) {
                contentType(ContentType.Application.Json)
                header(API_HEADER, session.deviceInfo.appId)
            }
            listener.onNewAdsLoaded(
                response.body<Session>().apply { this.deviceInfo = session.deviceInfo })
        } catch (e: Exception) {
            e.message?.let { AALogger.logError(it) }
            HttpErrorTracker.trackHttpError(
                e.cause.toString(),
                e.message.toString(),
                EventStrings.AD_GET_REQUEST_FAILED,
                refreshUrl
            )
            listener.onNewAdsLoadFailed()
        }
    }
}
