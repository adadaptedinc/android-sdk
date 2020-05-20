package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.event.BaseAdEventClient
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class AdEventClient private constructor(
        private val adEventSink: AdEventSink,
        private val transporter: TransporterCoroutineScope,
        private val impressionIdCounter: Counter
) : SessionClient.Listener, BaseAdEventClient {

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
            adEventSink.sendBatch(session, currentEvents)
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

    override fun onSessionInitFailed() {}

    @Synchronized
    override fun addListener(listener: Listener) {
        performAddListener(listener)
    }

    @Synchronized
    override fun removeListener(listener: Listener) {
        performRemoveListener(listener)
    }

    @Synchronized
    override fun trackImpression(ad: Ad) {
        transporter.dispatchToBackground {
            val count = impressionIdCounter.getIncrementedCountFor(ad.impressionId)
            fileEvent(ad, AdEvent.Types.IMPRESSION, count)
        }
    }

    @Synchronized
    override fun trackInteraction(ad: Ad) {
        transporter.dispatchToBackground {
            val count = impressionIdCounter.getCurrentCountFor(ad.impressionId)
            fileEvent(ad, AdEvent.Types.INTERACTION, count)
        }
    }

    @Synchronized
    override fun trackImpressionEnd(ad: Ad) {
        transporter.dispatchToBackground {
            val count = impressionIdCounter.getCurrentCountFor(ad.impressionId)
            fileEvent(ad, AdEvent.Types.IMPRESSION_END, count)
        }
    }

    @Synchronized
    override fun trackPopupBegin(ad: Ad) {
        transporter.dispatchToBackground {
            val count = impressionIdCounter.getCurrentCountFor(ad.impressionId)
            fileEvent(ad, AdEvent.Types.POPUP_BEGIN, count)
        }
    }

    @Synchronized
    override fun trackPopupEnd(ad: Ad) {
        transporter.dispatchToBackground {
            val count = impressionIdCounter.getCurrentCountFor(ad.impressionId)
            fileEvent(ad, AdEvent.Types.POPUP_END, count)
        }
    }

    @Synchronized
    override fun publishEvents() {
        transporter.dispatchToBackground {
            performPublishEvents() }
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
        SessionClient.addListener(this)
    }
}