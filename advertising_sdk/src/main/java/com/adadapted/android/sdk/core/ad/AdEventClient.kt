package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.core.concurrency.ThreadPoolInteractorExecuter
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class AdEventClient private constructor(private val adEventSink: AdEventSink) : SessionClient.Listener {
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

    companion object {
        private var instance: AdEventClient? = null

        fun createInstance(adEventSink: AdEventSink) {
            if (instance == null) {
                instance = AdEventClient(adEventSink)
            }
        }

        @Synchronized
        fun addListener(listener: Listener) {
            instance?.performAddListener(listener)
        }

        @Synchronized
        fun removeListener(listener: Listener) {
            instance?.performRemoveListener(listener)
        }

        @Synchronized
        fun trackImpression(ad: Ad) {
            if (instance == null) {
                return
            }
            ThreadPoolInteractorExecuter.getInstance().executeInBackground {
                val count = ImpressionIdCounter.instance?.getIncrementedCountFor(ad.impressionId) ?: 0
                instance?.fileEvent(ad, AdEvent.Types.IMPRESSION, count)
            }
        }

        @Synchronized
        fun trackImpressionEnd(ad: Ad) {
            if (instance == null) {
                return
            }
            ThreadPoolInteractorExecuter.getInstance().executeInBackground {
                val count = ImpressionIdCounter.instance?.getCurrentCountFor(ad.impressionId) ?: 0
                instance?.fileEvent(ad, AdEvent.Types.IMPRESSION_END, count)
            }
        }

        @Synchronized
        fun trackInteraction(ad: Ad) {
            if (instance == null) {
                return
            }
            ThreadPoolInteractorExecuter.getInstance().executeInBackground {
                val count = ImpressionIdCounter.instance?.getCurrentCountFor(ad.impressionId) ?: 0
                instance?.fileEvent(ad, AdEvent.Types.INTERACTION, count)
            }
        }

        @Synchronized
        fun trackPopupBegin(ad: Ad) {
            if (instance == null) {
                return
            }
            ThreadPoolInteractorExecuter.getInstance().executeInBackground {
                val count = ImpressionIdCounter.instance?.getCurrentCountFor(ad.impressionId) ?: 0
                instance?.fileEvent(ad, AdEvent.Types.POPUP_BEGIN, count)
            }
        }

        @Synchronized
        fun trackPopupEnd(ad: Ad) {
            if (instance == null) {
                return
            }
            ThreadPoolInteractorExecuter.getInstance().executeInBackground {
                val count = ImpressionIdCounter.instance?.getCurrentCountFor(ad.impressionId) ?: 0
                instance?.fileEvent(ad, AdEvent.Types.POPUP_END, count)
            }
        }

        @Synchronized
        fun publishEvents() {
            if (instance == null) {
                return
            }
            ThreadPoolInteractorExecuter.getInstance().executeInBackground { instance?.performPublishEvents() }
        }
    }

    init {
        events = HashSet()
        listeners = HashSet()
        SessionClient.addListener(this)
    }
}