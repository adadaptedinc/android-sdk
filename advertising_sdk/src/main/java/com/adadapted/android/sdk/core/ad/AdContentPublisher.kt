package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.core.concurrency.Transporter
import com.adadapted.android.sdk.core.log.AALogger

object AdContentPublisher {

    private var transporter: Transporter = Transporter()
    private val listeners: MutableSet<AdContentListener> = mutableSetOf()

    fun addListener(listener: AdContentListener) {
        if (listeners.none { it.listenerId == listener.listenerId }) {
            listeners.add(listener)
            AALogger.logDebug("Listener Count: ${listeners.size}")
        }
    }

    fun removeListener(listener: AdContentListener) {
        listeners.removeAll { it.listenerId == listener.listenerId }
    }

    fun publishContent(zoneId: String, content: AdContent) {
        if (content.hasNoItems()) {
            return
        }
        transporter.dispatchToMain {
            for (listener in listeners) {
                listener.onContentAvailable(zoneId, content)
            }
        }
    }

    fun publishNonContentNotification(zoneId: String, adId: String) {
        transporter.dispatchToMain {
            for (listener in listeners) {
                listener.onNonContentAction(zoneId, adId)
            }
        }
    }
}