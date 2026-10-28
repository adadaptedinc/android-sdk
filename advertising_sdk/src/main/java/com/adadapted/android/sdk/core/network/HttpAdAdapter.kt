package com.adadapted.android.sdk.core.network

import com.adadapted.android.sdk.core.interfaces.AdAdapter
import com.adadapted.android.sdk.core.interfaces.ZoneAdListener
import io.ktor.client.statement.HttpResponse
import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.ad.ZoneAdRequest
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.log.AALogger
import com.adadapted.android.sdk.core.network.HttpConnector.API_HEADER
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.core.ad.AdZoneResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.expectSuccess
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType

class HttpAdAdapter internal constructor(
    private val zoneAdRequestUrl: String,
    private val client: HttpClient
) : AdAdapter {
    constructor(zoneAdRequestUrl: String, httpConnector: HttpConnector) : this(zoneAdRequestUrl, httpConnector.client)

    override suspend fun requestAd(zoneId: String, listener: ZoneAdListener, storeId: String, contextId: String, extra: String) {
        val deviceInfo = DeviceInfoClient.getCachedDeviceInfo()
        val zoneAdRequest = ZoneAdRequest(
            sdkId = deviceInfo.sdkVersion,
            bundleId = deviceInfo.bundleId,
            userId = deviceInfo.udid,
            zoneId = zoneId,
            storeId = storeId,
            contextId = contextId,
            sessionId = SessionClient.getSessionId(),
            extra = extra
        )
        try {
            val response: HttpResponse = client.post(zoneAdRequestUrl) {
                //A non-2xx with a JSON body would otherwise decode to an empty Ad and read as a no-fill
                expectSuccess = true
                contentType(ContentType.Application.Json)
                setBody(zoneAdRequest)
                header(API_HEADER, deviceInfo.appId)
            }

            val adZoneResponse = response.body<AdZoneResponse>()
            listener.onAdLoaded(adZoneResponse.data)
        } catch (e: Exception) {
            e.message?.let { AALogger.logError(it) }
            HttpErrorTracker.trackHttpError(
                e.cause.toString(),
                e.message.toString(),
                EventStrings.AD_REQUEST_FAILED,
                zoneAdRequestUrl
            )
            listener.onAdLoadFailed()
        }
    }
}