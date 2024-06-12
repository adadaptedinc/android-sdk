package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.core.concurrency.Transporter

object AdContentPublisher {

    private var transporter: Transporter = Transporter()
    private val listeners: MutableSet<AdContentListener> = HashSet()

    fun addListener(listener: AdContentListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: AdContentListener) {
        listeners.remove(listener)
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