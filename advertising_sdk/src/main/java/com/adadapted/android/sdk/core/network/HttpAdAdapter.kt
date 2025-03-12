package com.adadapted.android.sdk.core.network

import com.adadapted.android.sdk.core.interfaces.AdAdapter
import com.adadapted.android.sdk.core.interfaces.ZoneAdListener
import io.ktor.client.statement.HttpResponse
import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.ad.ZoneAd
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.log.AALogger
import com.adadapted.android.sdk.core.network.HttpConnector.API_HEADER
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType

class HttpAdAdapter(
    private val zoneAdRequestUrl: String,
    private val httpConnector: HttpConnector
) : AdAdapter {
    override suspend fun requestAd(listener: ZoneAdListener) {
        try {
            val response: HttpResponse = httpConnector.client.post(zoneAdRequestUrl) {
                contentType(ContentType.Application.Json)
                header(API_HEADER, DeviceInfoClient.getCachedDeviceInfo())
            }
            listener.onAdLoaded(response.body<ZoneAd>())
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