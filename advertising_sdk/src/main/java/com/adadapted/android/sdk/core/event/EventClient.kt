package com.adadapted.android.sdk.core.event

import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.constants.EventStrings.SDK_EVENT_TYPE
import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.concurrency.Transporter
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.interfaces.EventClientListener
import com.adadapted.android.sdk.core.log.AALogger
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.core.session.SessionListener
import kotlin.jvm.Synchronized

object EventClient : SessionListener {

    private lateinit var eventAdapter: EventAdapter
    private var transporter: TransporterCoroutineScope = Transporter()
    private val listeners: MutableSet<EventClientListener> = HashSet()
    private val adEvents: MutableSet<AdEvent> = HashSet()
    private val sdkEvents: MutableSet<SdkEvent> = HashSet()
    private val sdkErrors: MutableSet<SdkError> = HashSet()
    private var session: Session? = null
    private var hasInstance: Boolean = false

    private fun performTrackSdkEvent(name: String, params: Map<String, String>) {
        sdkEvents.add(SdkEvent(SDK_EVENT_TYPE, name, params = params))
    }

    private fun performTrackSdkError(code: String, message: String, params: Map<String, String>) {
        AALogger.logError("App Error: $code - $message")
        sdkErrors.add(SdkError(code, message, params))
    }

    @Synchronized
    private fun performPublishSdkErrors() {
        if (session == null || sdkErrors.isEmpty()) {
            return
        }
        val currentSdkErrors: Set<SdkError> = HashSet(sdkErrors)
        sdkErrors.clear()
        session?.let {
            transporter.dispatchToThread {
                eventAdapter.publishSdkErrors(it, currentSdkErrors)
            }
        }
    }

    @Synchronized
    private fun performPublishSdkEvents() {
        if (session == null || sdkEvents.isEmpty()) {
            return
        }
        val currentSdkEvents: Set<SdkEvent> = HashSet(sdkEvents)
        sdkEvents.clear()
        session?.let {
            transporter.dispatchToThread {
                eventAdapter.publishSdkEvents(it, currentSdkEvents)
            }
        }
    }

    @Synchronized
    private fun performPublishAdEvents() {
        if (session == null || adEvents.isEmpty()) {
            return
        }
        val currentAdEvents: Set<AdEvent> = HashSet(adEvents)
        adEvents.clear()
        session?.let {
            transporter.dispatchToThread {
                eventAdapter.publishAdEvents(it, currentAdEvents)
            }
        }
    }

    @Synchronized
    private fun fileEvent(ad: Ad, eventType: String) {
        if (session == null) {
            return
        }
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

    private fun trackGAIDAvailability(session: Session) {
        if (!session.deviceInfo.isAllowRetargetingEnabled) {
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

    @Synchronized
    override fun onPublishEvents() {
        transporter.dispatchToThread {
            performPublishAdEvents()
            performPublishSdkEvents()
            performPublishSdkErrors()
        }
    }

    override fun onSessionAvailable(session: Session) {
        EventClient.session = session
        trackGAIDAvailability(session)
    }

    override fun onSessionExpired() {
        trackSdkEvent(EventStrings.EXPIRED_EVENT)
    }

    override fun onAdsAvailable(session: Session) {
        EventClient.session = session
    }

    fun trackSdkEvent(
        name: String,
        params: Map<String, String> = HashMap()
    ) {
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

    fun createInstance(eventAdapter: EventAdapter, transporter: TransporterCoroutineScope) {
        if (!hasInstance) {
            EventClient.eventAdapter = eventAdapter
            EventClient.transporter = transporter
            hasInstance = true
        }
    }

    init {
        SessionClient.addListener(this)
    }
}