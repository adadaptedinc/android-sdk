package com.adadapted.android.sdk

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.adadapted.android.sdk.config.Config
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.ad.AdEventClient
import com.adadapted.android.sdk.core.ad.ImpressionIdCounter
import com.adadapted.android.sdk.core.addit.AdditContent
import com.adadapted.android.sdk.core.addit.PayloadClient
import com.adadapted.android.sdk.core.concurrency.Transporter
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.event.AppEventClient
import com.adadapted.android.sdk.core.intercept.InterceptClient
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.core.session.SessionListener
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
import com.google.android.gms.ads.identifier.AdvertisingIdClient

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
            Toast.makeText(context, "AdAdapted API Key Is Missing", Toast.LENGTH_SHORT).show()
        }
        if (hasStarted) {
            if (!isProd) {
                Log.w(LOG_TAG, "AdAdapted Android Advertising SDK has already been started")
                AppEventClient.getInstance().trackError(EventStrings.MULTIPLE_SDK_STARTS, "App has attempted to start the SDK Multiple times")
            }
            return
        }
        hasStarted = true
        setupClients(context)
        SdkEventPublisher.getInstance().setListener(eventListener)
        AdditContentPublisher.getInstance().addListener(contentListener)
        PayloadClient.getInstance().pickupPayloads(object : PayloadClient.Callback {
            override fun onPayloadAvailable(content: List<AdditContent>) {
                if (content.isNotEmpty()) {
                    AdditContentPublisher.getInstance().publishAdditContent(content[0])
                }
            }
        })

        val startListener: SessionListener = object : SessionListener() {
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
        SessionClient.getInstance().start(startListener)
        AppEventClient.getInstance().trackSdkEvent(EventStrings.APP_OPENED)
        Log.i(LOG_TAG, String.format("AdAdapted Android Advertising SDK v%s initialized.", BuildConfig.VERSION_NAME))
    }

    private fun setupClients(context: Context) {
        Config.init(isProd)
        HttpRequestManager.createQueue(context.applicationContext)

        DeviceInfoClient.createInstance(context.applicationContext, apiKey, isProd, params, (AdvertisingIdClient::getAdvertisingIdInfo), transporter = Transporter())
        SessionClient.createInstance(HttpSessionAdapter(Config.getInitSessionUrl(), Config.getRefreshAdsUrl()), Transporter())
        AppEventClient.createInstance(HttpAppEventSink(Config.getAppEventsUrl(), Config.getAppErrorsUrl()), Transporter())
        ImpressionIdCounter.instance?.let { AdEventClient.createInstance(HttpAdEventSink(Config.getAdsEventUrl()), Transporter(), it) }
        InterceptClient.createInstance(HttpInterceptAdapter(Config.getRetrieveInterceptsUrl(), Config.getInterceptEventsUrl()), Transporter())
        PayloadClient.createInstance(HttpPayloadAdapter(Config.getPickupPayloadsUrl(), Config.getTrackingPayloadUrl()), AppEventClient.getInstance(), Transporter())
    }

    init {
        params = HashMap()
    }
}