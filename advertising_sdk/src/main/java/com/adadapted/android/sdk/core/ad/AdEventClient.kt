package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.core.session.SessionListener
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class AdEventClient private constructor(
        private val adEventSink: AdEventSink,
        private val transporter: TransporterCoroutineScope,
        private val impressionIdCounter: Counter
) : SessionListener() {

    interface Listener {
        fun onAdEventTracked(event: AdEvent?)
    }

    private val listeners: MutableSet<Listener>
    private val listenerLock: Lock = ReentrantLock()
    private val events: MutableSet<AdEvent>
    private val eventLock: Lock = ReentrantLock()
    private var session: Session? = null

    private fun fileEvent(ad: Ad, eventType: String, count: Int) {
        if (session == null) {
            return
        }
        eventLock.lock()
        try {
            val event = AdEvent(
                    ad.id,
                    ad.zoneId,
                    ad.impressionId + "::" + count,
                    eventType
            )
            events.add(event)
            notifyAdEventTracked(event)
        } finally {
            eventLock.unlock()
        }
    }

    private fun performPublishEvents() {
        if (session == null || events.isEmpty()) {
            return
        }
        eventLock.lock()
        try {
            val currentEvents: Set<AdEvent> = HashSet(events)
            events.clear()
            session?.let { adEventSink.sendBatch(it, currentEvents) }
        } finally {
            eventLock.unlock()
        }
    }

    private fun performAddListener(listener: Listener) {
        listenerLock.lock()
        try {
            listeners.add(listener)
        } finally {
            listenerLock.unlock()
        }
    }

    private fun performRemoveListener(listener: Listener) {
        listenerLock.lock()
        try {
            listeners.remove(listener)
        } finally {
            listenerLock.unlock()
        }
    }

    private fun notifyAdEventTracked(event: AdEvent) {
        listenerLock.lock()
        try {
            for (l in listeners) {
                l.onAdEventTracked(event)
            }
        } finally {
            listenerLock.unlock()
        }
    }

    override fun onPublishEvents() {
        transporter.dispatchToBackground {
            performPublishEvents() }
    }

    override fun onSessionAvailable(session: Session) {
        eventLock.lock()
        try {
            this.session = session
        } finally {
            eventLock.unlock()
        }
    }

    override fun onAdsAvailable(session: Session) {
        eventLock.lock()
        try {
            this.session = session
        } finally {
            eventLock.unlock()
        }
    }

    @Synchronized
    fun addListener(listener: Listener) {
        performAddListener(listener)
    }

    @Synchronized
    fun removeListener(listener: Listener) {
        performRemoveListener(listener)
    }

    @Synchronized
    fun trackImpression(ad: Ad) {
        transporter.dispatchToBackground {
            val count = impressionIdCounter.getIncrementedCountFor(ad.impressionId)
            fileEvent(ad, AdEvent.Types.IMPRESSION, count)
        }
    }

    @Synchronized
    fun trackInteraction(ad: Ad) {
        transporter.dispatchToBackground {
            val count = impressionIdCounter.getCurrentCountFor(ad.impressionId)
            fileEvent(ad, AdEvent.Types.INTERACTION, count)
        }
    }

    @Synchronized
    fun trackImpressionEnd(ad: Ad) {
        transporter.dispatchToBackground {
            val count = impressionIdCounter.getCurrentCountFor(ad.impressionId)
            fileEvent(ad, AdEvent.Types.IMPRESSION_END, count)
        }
    }

    @Synchronized
    fun trackPopupBegin(ad: Ad) {
        transporter.dispatchToBackground {
            val count = impressionIdCounter.getCurrentCountFor(ad.impressionId)
            fileEvent(ad, AdEvent.Types.POPUP_BEGIN, count)
        }
    }

    @Synchronized
    fun trackPopupEnd(ad: Ad) {
        transporter.dispatchToBackground {
            val count = impressionIdCounter.getCurrentCountFor(ad.impressionId)
            fileEvent(ad, AdEvent.Types.POPUP_END, count)
        }
    }

    companion object {
        private lateinit var instance: AdEventClient

        fun createInstance(
                adEventSink: AdEventSink,
                transporter: TransporterCoroutineScope,
                impressionIdCounter: Counter) {
            this.instance = AdEventClient(adEventSink, transporter, impressionIdCounter)
        }

        fun getInstance(): AdEventClient {
            return instance
        }
    }

    init {
        events = HashSet()
        listeners = HashSet()
        SessionClient.getInstance().addListener(this)
    }
}