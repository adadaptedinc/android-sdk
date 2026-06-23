package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.core.concurrency.Transporter
import com.adadapted.android.sdk.core.log.AALogger
import kotlin.jvm.Synchronized

object AdContentPublisher {

    private var transporter: Transporter = Transporter()
    private val listeners: MutableSet<AdContentListener> = mutableSetOf()

    @Synchronized
    fun addListener(listener: AdContentListener) {
        if (listeners.none { it.listenerId == listener.listenerId }) {
            listeners.add(listener)
            AALogger.logDebug("Listener Count: ${listeners.size}")
        }
    }

    @Synchronized
    fun removeListener(listener: AdContentListener) {
        listeners.removeAll { it.listenerId == listener.listenerId }
    }

    fun publishContent(zoneId: String, content: AdContent) {
        if (content.hasNoItems()) {
            return
        }
        val currentListeners: Set<AdContentListener> = synchronized(this) { listeners.toSet() }
        transporter.dispatchToMain {
            for (listener in currentListeners) {
                listener.onContentAvailable(zoneId, content)
            }
        }
    }

    fun publishNonContentNotification(zoneId: String, adId: String) {
        val currentListeners: Set<AdContentListener> = synchronized(this) { listeners.toSet() }
        transporter.dispatchToMain {
            for (listener in currentListeners) {
                listener.onNonContentAction(zoneId, adId)
            }
        }
    }
}