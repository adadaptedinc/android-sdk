package com.adadapted.android.sdk.ui.messaging

import android.os.Handler
import android.os.Looper
import com.adadapted.android.sdk.core.ad.AdEvent
import com.adadapted.android.sdk.core.ad.AdEventClient
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class SdkEventPublisher private constructor() : AdEventClient.Listener {
    private object EventTypes {
        const val IMPRESSION = "impression"
        const val CLICK = "click"
    }
    private var listener: AaSdkEventListener? = null
    private val lock: Lock = ReentrantLock()

    fun setListener(listener: AaSdkEventListener) {
        lock.lock()
        try {
            this.listener = listener
        } finally {
            lock.unlock()
        }
    }

    override fun onAdEventTracked(event: AdEvent?) {
        if (listener == null || event == null) {
            return
        }
        lock.lock()
        try {
            when (event.eventType) {
                AdEvent.Types.IMPRESSION -> notifyNextAdEvent(event.zoneId, EventTypes.IMPRESSION)
                AdEvent.Types.INTERACTION -> notifyNextAdEvent(event.zoneId, EventTypes.CLICK)
            }
        } finally {
            lock.unlock()
        }
    }

    private fun notifyNextAdEvent(zoneId: String, eventType: String) {
        Handler(Looper.getMainLooper()).post { listener?.onNextAdEvent(zoneId, eventType) }
    }

    companion object {
        private lateinit var instance: SdkEventPublisher

        fun getInstance(): SdkEventPublisher {
            if (!this::instance.isInitialized) {
                createInstance()
            }
            return instance
        }

        fun createInstance() {
            instance = SdkEventPublisher()
        }
    }

    init {
        AdEventClient.getInstance().addListener(this)
    }
}