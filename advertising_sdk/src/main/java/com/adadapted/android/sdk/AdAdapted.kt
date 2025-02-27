package com.adadapted.android.sdk

import android.content.Context
import com.adadapted.android.sdk.constants.Config
import com.adadapted.android.sdk.core.atl.AddItContentPublisher
import com.adadapted.android.sdk.core.concurrency.Transporter
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.device.DeviceInfoExtractor
import com.adadapted.android.sdk.core.event.EventBroadcaster
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.interfaces.AaSdkAdditContentListener
import com.adadapted.android.sdk.core.interfaces.AaSdkEventListener
import com.adadapted.android.sdk.core.interfaces.AaSdkSessionListener
import com.adadapted.android.sdk.core.keyword.InterceptClient
import com.adadapted.android.sdk.core.keyword.KeywordInterceptMatcher
import com.adadapted.android.sdk.core.log.AALogger
import com.adadapted.android.sdk.core.network.HttpConnector
import com.adadapted.android.sdk.core.network.HttpEventAdapter
import com.adadapted.android.sdk.core.network.HttpInterceptAdapter
import com.adadapted.android.sdk.core.network.HttpPayloadAdapter
import com.adadapted.android.sdk.core.network.HttpSessionAdapter
import com.adadapted.android.sdk.core.payload.PayloadClient
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.core.session.SessionListener

object AdAdapted {
    enum class Env { PROD, DEV }
    private var hasStarted = false
    private var apiKey: String = ""
    private var isProd = false
    private var customIdentifier: String = ""
    private var isKeywordInterceptEnabled = false
    private var isPayloadEnabled = false
    private var params: Map<String, String> = HashMap()
    lateinit var sessionListener: AaSdkSessionListener
    private lateinit var eventListener: AaSdkEventListener
    private lateinit var contentListener: AaSdkAdditContentListener

    fun withAppId(key: String): AdAdapted {
        this.apiKey = key
        return this
    }

    fun inEnv(env: Env): AdAdapted {
        isProd = env == Env.PROD
        return this
    }

    fun setSdkSessionListener(listener: AaSdkSessionListener): AdAdapted {
        sessionListener = listener
        return this
    }

    fun enableKeywordIntercept(value: Boolean): AdAdapted {
        isKeywordInterceptEnabled = value
        return this
    }

    fun enablePayloads(value: Boolean): AdAdapted {
        isPayloadEnabled = value
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

    fun setOptionalParams(params:HashMap<String, String>): AdAdapted {
        this.params = params
        return this
    }

    fun enableDebugLogging(): AdAdapted {
        AALogger.enableDebugLogging()
        return this
    }

    fun setCustomIdentifier(identifier: String): AdAdapted {
        customIdentifier = identifier
        return this
    }

    fun disableAdTracking(context: Context): AdAdapted {
        setAdTracking(context, true)
        return this
    }

    fun enableAdTracking(context: Context): AdAdapted {
        setAdTracking(context, false)
        return this
    }

    fun start(context: Context) {
        if (apiKey.isEmpty()) {
            AALogger.logError("The AdAdapted Api Key is missing or NULL")
        }
        if (hasStarted) {
            return
        }
        hasStarted = true
        setupClients(context)
        eventListener.let { EventBroadcaster.setListener(it) }
        contentListener.let { AddItContentPublisher.addListener(it) }

        if (isPayloadEnabled) {
            PayloadClient.pickupPayloads {
                if (it.isNotEmpty()) {
                    for (content in it) {
                        AddItContentPublisher.publishAddItContent(content)
                    }
                }
            }
        }

        val startListener: SessionListener = object : SessionListener {
            override fun onSessionAvailable(session: Session) {
                sessionListener.onHasAdsToServe(session.hasActiveCampaigns(), session.getZonesWithAds())
                if (session.hasActiveCampaigns() && session.getZonesWithAds().isEmpty()) {
                    AALogger.logError("The session has ads to show but none were loaded properly. Is an obfuscation tool obstructing the AdAdapted Library?")
                }
            }

            override fun onAdsAvailable(session: Session) {
                sessionListener.onHasAdsToServe(session.hasActiveCampaigns(), session.getZonesWithAds())
            }

            override fun onSessionInitFailed() {
                sessionListener.onHasAdsToServe(false, listOf())
            }
        }
        SessionClient.start(startListener)

        if (isKeywordInterceptEnabled) {
            KeywordInterceptMatcher.match("INIT") //init the matcher
        }
        AALogger.logInfo("AdAdapted Android SDK ${Config.LIBRARY_VERSION} initialized.")
    }

    private fun setAdTracking(context: Context, value: Boolean) {
        val sharedPref =
            context.getSharedPreferences(Config.AASDK_PREFS_KEY, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean(Config.AASDK_PREFS_TRACKING_DISABLED_KEY, value)
            apply()
        }
    }

    private fun setupClients(context: Context) {
        Config.init(isProd)

        val deviceInfoExtractor = DeviceInfoExtractor(context)
        DeviceInfoClient.createInstance(
            apiKey,
            isProd,
            params,
            customIdentifier,
            deviceInfoExtractor,
            Transporter()
        )
        SessionClient.createInstance(
            HttpSessionAdapter(
                Config.getInitSessionUrl(),
                Config.getRefreshAdsUrl(),
                HttpConnector
            ), Transporter()
        )
        EventClient.createInstance(
            HttpEventAdapter(
                Config.getAdEventsUrl(),
                Config.getSdkEventsUrl(),
                Config.getSdkErrorsUrl(),
                HttpConnector
            ), Transporter()
        )
        InterceptClient.createInstance(
            HttpInterceptAdapter(
                Config.getRetrieveInterceptsUrl(),
                Config.getInterceptEventsUrl(),
                HttpConnector
            ), Transporter()
        )
        PayloadClient.createInstance(
            HttpPayloadAdapter(
                Config.getPickupPayloadsUrl(),
                Config.getTrackingPayloadUrl(),
                HttpConnector
            ), EventClient, Transporter()
        )
    }
}

