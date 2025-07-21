package com.adadapted.android.sdk

import android.content.Context
import androidx.lifecycle.ProcessLifecycleOwner
import com.adadapted.android.sdk.constants.Config
import com.adadapted.android.sdk.core.ad.AdClient
import com.adadapted.android.sdk.core.atl.AddItContentPublisher
import com.adadapted.android.sdk.core.concurrency.Transporter
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.device.DeviceInfoExtractor
import com.adadapted.android.sdk.core.event.EventBroadcaster
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.interfaces.AaSdkAdditContentListener
import com.adadapted.android.sdk.core.interfaces.AaSdkEventListener
import com.adadapted.android.sdk.core.interfaces.DeviceCallback
import com.adadapted.android.sdk.core.keyword.InterceptClient
import com.adadapted.android.sdk.core.keyword.KeywordInterceptMatcher
import com.adadapted.android.sdk.core.log.AALogger
import com.adadapted.android.sdk.core.network.HttpAdAdapter
import com.adadapted.android.sdk.core.network.HttpConnector
import com.adadapted.android.sdk.core.network.HttpEventAdapter
import com.adadapted.android.sdk.core.network.HttpInterceptAdapter
import com.adadapted.android.sdk.core.network.HttpPayloadAdapter
import com.adadapted.android.sdk.core.payload.PayloadClient
import com.adadapted.android.sdk.core.session.SessionClient

object AdAdapted {
    enum class Env { PROD, DEV }
    private var hasStarted = false
    private var apiKey: String = ""
    private var isProd = false
    private var customIdentifier: String = ""
    private var isKeywordInterceptEnabled = false
    private var isPayloadEnabled = false
    private var params: Map<String, String> = HashMap()
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

        DeviceInfoClient.getDeviceInfo(object : DeviceCallback {
            override fun onDeviceInfoCollected(deviceInfo: DeviceInfo) {
                setupDependentClients()
            }
        })

        ProcessLifecycleOwner.get().lifecycle.addObserver(SessionClient)
    }

    private fun setupDependentClients() {
        AdClient.createInstance(
            HttpAdAdapter(
                "https://dev.adadapted.dev/api/ad-service/v100-alpha/ad/retrieve", //TODO TEMP REMOVE
                //Config.getRetrieveAdsUrl(),
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
                "https://dev.adadapted.dev/api/ad-service/v100-alpha/intercept/retrieve", //TODO TEMP REMOVE
                //Config.getRetrieveInterceptsUrl(),
                Config.getInterceptEventsUrl(),
                HttpConnector
            ), Transporter(),
            isKeywordInterceptEnabled
        )
        PayloadClient.createInstance(
            HttpPayloadAdapter(
                Config.getPickupPayloadsUrl(),
                Config.getTrackingPayloadUrl(),
                HttpConnector
            ), EventClient, Transporter()
        )

        if (isKeywordInterceptEnabled) {
            KeywordInterceptMatcher.match("INIT") //init the matcher
        }

        if (isPayloadEnabled) {
            PayloadClient.pickupPayloads {
                if (it.isNotEmpty()) {
                    for (content in it) {
                        AddItContentPublisher.publishAddItContent(content)
                    }
                }
            }
        }
    }
}