package com.adadapted.android.sdk

import android.content.Context
import android.util.Log
import com.adadapted.android.sdk.config.Config
import com.adadapted.android.sdk.core.ad.AdEventClient
import com.adadapted.android.sdk.core.ad.ImpressionIdCounter
import com.adadapted.android.sdk.core.addit.PayloadClient
import com.adadapted.android.sdk.core.concurrency.Transporter
import com.adadapted.android.sdk.core.event.AdAdaptedEventClient
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.intercept.InterceptClient
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.ext.http.HttpAdEventSink
import com.adadapted.android.sdk.ext.http.HttpAppEventSink
import com.adadapted.android.sdk.ext.http.HttpInterceptAdapter
import com.adadapted.android.sdk.ext.http.HttpPayloadAdapter
import com.adadapted.android.sdk.ext.http.HttpRequestManager
import com.adadapted.android.sdk.ext.http.HttpSessionAdapter
import com.adadapted.android.sdk.ui.messaging.AaSdkAdditContentListener
import com.adadapted.android.sdk.ui.messaging.AaSdkEventListener
import com.adadapted.android.sdk.ui.messaging.AaSdkSessionListener
import com.adadapted.android.sdk.ui.messaging.AdditContentPublisher
import com.adadapted.android.sdk.ui.messaging.SdkEventPublisher

object AdAdapted {

    enum class Env { PROD, DEV }

    private var hasStarted = false
    private var apiKey: String = ""
    private var isProd = false
    private val params: Map<String, String>
    private var sessionListener: AaSdkSessionListener? = null
    private var eventListener: AaSdkEventListener? = null
    private var contentListener: AaSdkAdditContentListener? = null
    private val LOG_TAG = AdAdapted::class.java.name

    fun withAppId(key: String): AdAdapted {
        this.apiKey = key
        return this
    }

    fun inEnv(environment: Env): AdAdapted {
        isProd = environment == Env.PROD
        return this
    }

    fun setSdkSessionListener(listener: AaSdkSessionListener): AdAdapted {
        sessionListener = listener
        return this
    }

    fun setSdkEventListener(listener: AaSdkEventListener): AdAdapted {
        eventListener = listener
        return this
    }

    fun setSdkAdditContentListener(listener: AaSdkAdditContentListener): AdAdapted {
        contentListener = listener
        return this
    }

    fun start(context: Context) {
        if (apiKey.isEmpty()) {
            Log.e(LOG_TAG, "The Api Key cannot be NULL")
            return
        }
        if (hasStarted) {
            if (!isProd) {
                Log.w(LOG_TAG, "AdAdapted Android Advertising SDK has already been started")
                AppEventClient.getInstance().trackError("MULTIPLE_SDK_STARTS", "App has attempted to start the SDK Multiple times")
            }
            return
        }
        hasStarted = true
        setupClients(context)
        SdkEventPublisher.getInstance().setListener(eventListener)
        AdditContentPublisher.getInstance().addListener(contentListener)
        PayloadClient.pickupPayloads { content ->
            if (content.size > 0) {
                AdditContentPublisher.getInstance().publishAdditContent(content[0])
            }
        }
        val startListener: SessionClient.Listener = object : SessionClient.Listener {
            override fun onSessionAvailable(session: Session) {
                sessionListener?.onHasAdsToServe(session.hasActiveCampaigns())
            }

            override fun onAdsAvailable(session: Session) {
                sessionListener?.onHasAdsToServe(session.hasActiveCampaigns())
            }

            override fun onSessionInitFailed() {
                sessionListener?.onHasAdsToServe(false)
            }
        }
        SessionClient.start(
                context.applicationContext,
                apiKey,
                isProd,
                params,
                startListener)
        AppEventClient.getInstance().trackSdkEvent("app_opened")
        Log.i(LOG_TAG, String.format("AdAdapted Android Advertising SDK v%s initialized.", BuildConfig.VERSION_NAME))
    }

    private fun setupClients(context: Context) {
        Config.init(isProd)
        HttpRequestManager.createQueue(context.applicationContext)

        SessionClient.createInstance(HttpSessionAdapter(Config.getInitSessionUrl(), Config.getRefreshAdsUrl()))
        AppEventClient.createInstance(HttpAppEventSink(Config.getAppEventsUrl(), Config.getAppErrorsUrl()), Transporter())
        ImpressionIdCounter.instance?.let { AdEventClient.createInstance(HttpAdEventSink(Config.getAdsEventUrl()), Transporter(), it) }
        InterceptClient.createInstance(HttpInterceptAdapter(Config.getRetrieveInterceptsUrl(), Config.getInterceptEventsUrl()))
        PayloadClient.createInstance(HttpPayloadAdapter(Config.getPickupPayloadsUrl(), Config.getTrackingPayloadUrl()))
        AdAdaptedEventClient.createInstance(AdEventClient.getInstance(), AppEventClient.getInstance())
    }

    init {
        params = HashMap()
    }
}