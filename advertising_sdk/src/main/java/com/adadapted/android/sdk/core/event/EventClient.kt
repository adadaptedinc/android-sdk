package com.adadapted.android.sdk.core.event

import com.adadapted.android.sdk.constants.Config
import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.constants.EventStrings.SDK_EVENT_TYPE
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.concurrency.Timer
import com.adadapted.android.sdk.core.concurrency.Transporter
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.interfaces.EventClientListener
import com.adadapted.android.sdk.core.log.AALogger
import com.adadapted.android.sdk.core.session.NewSessionClient
import kotlin.jvm.Synchronized

object EventClient {

    private lateinit var eventAdapter: EventAdapter
    private var transporter: TransporterCoroutineScope = Transporter()
    private val listeners: MutableSet<EventClientListener> = HashSet()
    private val adEvents: MutableSet<AdEvent> = HashSet()
    private val sdkEvents: MutableSet<SdkEvent> = HashSet()
    private val sdkErrors: MutableSet<SdkError> = HashSet()
    private var eventTimerRunning: Boolean = false

    @Synchronized
    private fun performTrackSdkEvent(name: String, params: Map<String, String>) {
        sdkEvents.add(SdkEvent(SDK_EVENT_TYPE, name, params = params))
    }

    @Synchronized
    private fun performTrackSdkError(code: String, message: String, params: Map<String, String>) {
        AALogger.logError("App Error: $code - $message")
        sdkErrors.add(SdkError(code, message, params))
    }

    @Synchronized
    private fun performPublishSdkErrors() {
        if (sdkErrors.isEmpty()) {
            return
        }
        val currentSdkErrors: Set<SdkError> = sdkErrors.map { it.copy() }.toSet()
        sdkErrors.clear()
        transporter.dispatchToThread {
            DeviceInfoClient.getCachedDeviceInfo().let { eventAdapter.publishSdkErrors(NewSessionClient.getSessionId(), it, currentSdkErrors) }
        }
    }

    @Synchronized
    private fun performPublishSdkEvents() {
        if (sdkEvents.isEmpty()) {
            return
        }
        val currentSdkEvents: Set<SdkEvent> = sdkEvents.map { it.copy() }.toSet()
        sdkEvents.clear()
        transporter.dispatchToThread {
            DeviceInfoClient.getCachedDeviceInfo().let { eventAdapter.publishSdkEvents(NewSessionClient.getSessionId(), it, currentSdkEvents) }
        }
    }

    @Synchronized
    private fun performPublishAdEvents() {
        if (adEvents.isEmpty()) {
            return
        }
        val currentAdEvents: Set<AdEvent> = adEvents.map { it.copy() }.toSet()
        adEvents.clear()
        transporter.dispatchToThread {
            DeviceInfoClient.getCachedDeviceInfo().let { eventAdapter.publishAdEvents(NewSessionClient.getSessionId(), it, currentAdEvents) }
        }
    }

    @Synchronized
    private fun fileEvent(ad: Ad, eventType: String) {
        val event = AdEvent(
            ad.id,
            ad.zoneId,
            ad.impressionId,
            eventType
        )
        adEvents.add(event)
        notifyAdEventTracked(event)
    }

    private fun performAddListener(listener: EventClientListener) {
        listeners.add(listener)
    }

    private fun performRemoveListener(listener: EventClientListener) {
        listeners.remove(listener)
    }

    private fun trackGAIDAvailability() {
        if (!DeviceInfoClient.getCachedDeviceInfo().isAllowRetargetingEnabled) {
            trackSdkError(
                EventStrings.GAID_UNAVAILABLE,
                "GAID and/or tracking has been disabled for this device."
            )
        }
    }

    @Synchronized
    private fun notifyAdEventTracked(event: AdEvent) {
        for (l in listeners) {
            l.onAdEventTracked(event)
        }
    }

    private fun startPublishTimer() {
        if (eventTimerRunning) {
            return
        }
        eventTimerRunning = true

        val eventTimer = Timer(
            { onPublishEvents() },
            repeatMillis = Config.DEFAULT_EVENT_POLLING,
            delayMillis = Config.DEFAULT_EVENT_POLLING
        )
        eventTimer.startTimer()
    }

    @Synchronized
    fun onPublishEvents() {
        transporter.dispatchToThread {
            performPublishAdEvents()
            performPublishSdkEvents()
            performPublishSdkErrors()
        }
    }

    fun trackSdkEvent(name: String, params: Map<String, String> = HashMap()) {
        transporter.dispatchToThread {
            performTrackSdkEvent(name, params)
        }
    }

    fun trackSdkError(code: String, message: String, params: Map<String, String> = HashMap()) {
        transporter.dispatchToThread {
            performTrackSdkError(code, message, params)
        }
    }

    fun addListener(listener: EventClientListener) {
        performAddListener(listener)
    }

    fun removeListener(listener: EventClientListener) {
        performRemoveListener(listener)
    }

    fun trackImpression(ad: Ad) {
        AALogger.logDebug("Ad Impression Tracked.")
        transporter.dispatchToThread {
            fileEvent(ad, AdEventTypes.IMPRESSION)
        }
    }

    fun trackInvisibleImpression(ad: Ad) {
        AALogger.logDebug("Ad Invisible Impression Tracked.")
        transporter.dispatchToThread {
            fileEvent(ad, AdEventTypes.INVISIBLE_IMPRESSION)
        }
    }

    fun trackInteraction(ad: Ad) {
        AALogger.logDebug("Ad Interaction Tracked.")
        transporter.dispatchToThread {
            fileEvent(ad, AdEventTypes.INTERACTION)
        }
    }

    fun trackPopupBegin(ad: Ad) {
        transporter.dispatchToThread {
            fileEvent(ad, AdEventTypes.POPUP_BEGIN)
        }
    }

    fun trackRecipeContextEvent(contextId: String, zoneId: String) {
        val eventParams: MutableMap<String, String> = mutableMapOf()
        eventParams[RecipeSources.CONTEXT_ID] = contextId
        eventParams[RecipeSources.ZONE_ID] = zoneId
        trackSdkEvent(EventStrings.RECIPE_CONTEXT, eventParams)
    }

    fun createInstance(eventAdapter: EventAdapter, transporter: TransporterCoroutineScope) {
        EventClient.eventAdapter = eventAdapter
        EventClient.transporter = transporter
    }

    init {
        startPublishTimer()
        trackGAIDAvailability()
    }
}