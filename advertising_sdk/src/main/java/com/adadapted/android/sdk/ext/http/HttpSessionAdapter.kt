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
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest

class HttpSessionAdapter(private val initUrl: String, private val refreshUrl: String, private var sessionBuilder: JsonSessionBuilder? = null, private val httpQueueManager: HttpQueueManager = HttpRequestManager) : SessionAdapter {

    override fun sendInit(deviceInfo: DeviceInfo, listener: SessionInitListener) {
        val requestBuilder = JsonSessionRequestBuilder()
        sessionBuilder = JsonSessionBuilder(deviceInfo)
        val json = requestBuilder.buildSessionInitRequest(deviceInfo)
        val jsonRequest = JsonObjectRequest(
                Request.Method.POST,
                initUrl,
                json,
                Response.Listener { response ->
                    val session = sessionBuilder?.buildSession(response)
                    session?.let { listener.onSessionInitialized(it) }
                }, Response.ErrorListener { error ->
            HttpErrorTracker.trackHttpError(error, initUrl, EventStrings.SESSION_REQUEST_FAILED, LOGTAG)
            listener.onSessionInitializeFailed()
        })
        httpQueueManager.queueRequest(jsonRequest)
    }

    override fun sendRefreshAds(session: Session, listener: AdGetListener) {
        if (sessionBuilder == null) {
            return
        }
        val url = refreshUrl + String.format("?aid=%s", session.deviceInfo.appId) + String.format("&uid=%s", session.deviceInfo.udid) + String.format("&sid=%s", session.id) + String.format("&sdk=%s", session.deviceInfo.sdkVersion)
        val request = JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                Response.Listener { response ->
                    val responseSession = sessionBuilder?.buildSession(response)
                    responseSession?.let { listener.onNewAdsLoaded(it) }
                },
                Response.ErrorListener { error ->
                    HttpErrorTracker.trackHttpError(error, refreshUrl, EventStrings.AD_GET_REQUEST_FAILED, LOGTAG)
                    listener.onNewAdsLoadFailed()
                }
        )
        httpQueueManager.queueRequest(request)
    }

    companion object {
        private val LOGTAG = HttpSessionAdapter::class.java.name
    }
}