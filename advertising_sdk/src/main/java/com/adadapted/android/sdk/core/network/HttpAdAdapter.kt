package com.adadapted.android.sdk.core.network

import android.util.Log
import com.adadapted.android.sdk.core.interfaces.AdAdapter
import com.adadapted.android.sdk.core.interfaces.ZoneAdListener
import io.ktor.client.statement.HttpResponse
import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.ad.AdResponse
import com.adadapted.android.sdk.core.ad.ZoneAdRequest
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.log.AALogger
import com.adadapted.android.sdk.core.network.HttpConnector.API_HEADER
import com.adadapted.android.sdk.core.session.NewSessionClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType

class HttpAdAdapter(
    private val zoneAdRequestUrl: String,
    private val httpConnector: HttpConnector
) : AdAdapter {
    override suspend fun requestAd(zoneId: String, listener: ZoneAdListener, storeId: String, contextId: String, extra: String) {
        var deviceInfo = DeviceInfoClient.getCachedDeviceInfo()
        val zoneAdRequest = ZoneAdRequest(
            sdkId = deviceInfo.appId,
            bundleId = "",
            userId = deviceInfo.udid,
            zoneId = zoneId,
            storeId = storeId,
            contextId = contextId,
            sessionId = NewSessionClient.getSessionId(),
            extra = extra
        )
        try {
            val response: HttpResponse = httpConnector.client.post(zoneAdRequestUrl) {
                contentType(ContentType.Application.Json)
                setBody(zoneAdRequest)
                header(API_HEADER, deviceInfo.appId)
            }
            val rawJson = response.body<String>()
            Log.e("Ad junk", rawJson) //TODO cleanup
            listener.onAdLoaded(response.body<AdResponse>().data)
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