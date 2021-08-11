package com.adadapted.android.sdk.ext.http

import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionAdapter
import com.adadapted.android.sdk.core.session.SessionAdapter.AdGetListener
import com.adadapted.android.sdk.core.session.SessionAdapter.SessionInitListener
import com.adadapted.android.sdk.ext.json.JsonSessionBuilder
import com.adadapted.android.sdk.ext.json.JsonSessionRequestBuilder
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest

class HttpSessionAdapter(private val initUrl: String, private val refreshUrl: String, private var sessionBuilder: JsonSessionBuilder? = null, private val httpQueueManager: HttpQueueManager = HttpRequestManager) : SessionAdapter {
    private val LOGTAG = HttpSessionAdapter::class.java.name

    override fun sendInit(deviceInfo: DeviceInfo, listener: SessionInitListener) {
        val requestBuilder = JsonSessionRequestBuilder()
        sessionBuilder = JsonSessionBuilder(deviceInfo)
        val json = requestBuilder.buildSessionInitRequest(deviceInfo)
        val jsonRequest = JsonObjectRequest(
            Request.Method.POST,
            initUrl,
            json,
            { response ->
                val session = sessionBuilder?.buildSession(response)
                session?.let { listener.onSessionInitialized(it) }
            }, { error ->
                HttpErrorTracker.trackHttpError(
                    error,
                    initUrl,
                    EventStrings.SESSION_REQUEST_FAILED,
                    LOGTAG
                )
                listener.onSessionInitializeFailed()
            })
        httpQueueManager.queueRequest(jsonRequest)
    }

    override fun sendRefreshAds(session: Session, listener: AdGetListener) {
        if (sessionBuilder == null) {
            return
        }
        val url = refreshUrl + String.format("?aid=%s", session.getDeviceInfo().appId) + String.format("&uid=%s", session.getDeviceInfo().udid) + String.format("&sid=%s", session.id) + String.format("&sdk=%s", session.getDeviceInfo().sdkVersion)
        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                val responseSession = sessionBuilder?.buildSession(response)
                responseSession?.let { listener.onNewAdsLoaded(it) }
            },
            { error ->
                HttpErrorTracker.trackHttpError(
                    error,
                    refreshUrl,
                    EventStrings.AD_GET_REQUEST_FAILED,
                    LOGTAG
                )
                listener.onNewAdsLoadFailed()
            }
        )
        httpQueueManager.queueRequest(request)
    }
}