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
import com.adadapted.android.sdk.core.session.SessionClient
import kotlin.jvm.Synchronized

object EventClient {

    private lateinit var eventAdapter: EventAdapter
    private var transporter: TransporterCoroutineScope = Transporter()
    private val listeners: MutableSet<EventClientListener> = HashSet()
    private val adEvents: MutableList<AdEvent> = mutableListOf()
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
        if (sdkErrors.isEmpty() || (!::eventAdapter.isInitialized)) {
            return
        }
        val currentSdkErrors: Set<SdkError> = sdkErrors.map { it.copy() }.toSet()
        sdkErrors.clear()
        transporter.dispatchToThread {
            DeviceInfoClient.getCachedDeviceInfo().let { eventAdapter.publishSdkErrors(SessionClient.getSessionId(), it, currentSdkErrors) }
        }
    }

    @Synchronized
    private fun performPublishSdkEvents() {
        if (sdkEvents.isEmpty() || (!::eventAdapter.isInitialized)) {
            return
        }
        val currentSdkEvents: Set<SdkEvent> = sdkEvents.map { it.copy() }.toSet()
        sdkEvents.clear()
        transporter.dispatchToThread {
            DeviceInfoClient.getCachedDeviceInfo().let { eventAdapter.publishSdkEvents(SessionClient.getSessionId(), it, currentSdkEvents) }
        }
    }

    @Synchronized
    private fun performPublishAdEvents() {
        if (adEvents.isEmpty() || (!::eventAdapter.isInitialized)) {
            return
        }
        val currentAdEvents: List<AdEvent> = adEvents.toList()
        adEvents.clear()
        transporter.dispatchToThread {
            DeviceInfoClient.getCachedDeviceInfo().let { eventAdapter.publishAdEvents(SessionClient.getSessionId(), it, currentAdEvents) }
        }
    }

    @Synchronized
    private fun fileEvent(event: AdEvent) {
        adEvents.add(event)
        transporter.dispatchToThread {
            notifyAdEventTracked(event)
        }
    }

    @Synchronized
    private fun performAddListener(listener: EventClientListener) {
        listeners.add(listener)
    }

    @Synchronized
    private fun performRemoveListener(listener: EventClientListener) {
        listeners.remove(listener)
    }

    @Synchronized
    private fun notifyAdEventTracked(event: AdEvent) {
        for (l in listeners) {
            l.onAdEventTracked(event)
        }
    }

    @Synchronized
    private fun startPublishTimer() {
        if (eventTimerRunning) {
            return
        }
        eventTimerRunning = true

        Timer(
            { onPublishEvents() },
            repeatSeconds = Config.DEFAULT_EVENT_POLLING_SECONDS,
            delaySeconds = Config.DEFAULT_EVENT_POLLING_SECONDS
        )
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
        ad.setImpressionTracked()
        fileEvent(AdEvent.forAd(ad, AdEventTypes.IMPRESSION))
    }

    fun trackImpressionEnd(ad: Ad) {
        if (!ad.claimImpressionEnd()) {
            return
        }
        AALogger.logDebug("Ad Impression End Tracked.")
        fileEvent(AdEvent.forAd(ad, AdEventTypes.IMPRESSION_END))
    }

    fun trackImpressionEndAndPublish(ad: Ad) {
        trackImpressionEnd(ad)
        onPublishEvents()
    }

    fun trackInteraction(ad: Ad) {
        AALogger.logDebug("Ad Interaction Tracked.")
        fileEvent(AdEvent.forAd(ad, AdEventTypes.INTERACTION))
    }

    fun trackPopupBegin(ad: Ad) {
        fileEvent(AdEvent.forAd(ad, AdEventTypes.POPUP_BEGIN))
    }

    fun trackZoneMounted(zoneId: String) {
        AALogger.logDebug("Zone Mounted Tracked.")
        fileEvent(AdEvent.forZone(zoneId, AdEventTypes.ZONE_MOUNTED))
    }

    fun trackZoneUnmounted(zoneId: String) {
        AALogger.logDebug("Zone Unmounted Tracked.")
        fileEvent(AdEvent.forZone(zoneId, AdEventTypes.ZONE_UNMOUNTED))
    }

    fun trackZoneUnfilled(zoneId: String, reason: String) {
        AALogger.logDebug("Zone Unfilled Tracked: $reason")
        fileEvent(AdEvent.forZone(zoneId, AdEventTypes.ZONE_UNFILLED, reason))
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
        startPublishTimer()
    }
}