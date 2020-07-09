package com.adadapted.android.sdk.ui.messaging

import android.os.Handler
import android.os.Looper
import com.adadapted.android.sdk.core.ad.AdContent
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class AdContentPublisher private constructor() {
    private val listeners: MutableSet<AdContentListener> = HashSet()
    private val lock: Lock = ReentrantLock()

    fun addListener(listener: AdContentListener) {
        lock.lock()
        try {
            listeners.add(listener)
        } finally {
            lock.unlock()
        }
    }

    fun removeListener(listener: AdContentListener) {
        lock.lock()
        try {
            listeners.remove(listener)
        } finally {
            lock.unlock()
        }
    }

    fun publishContent(zoneId: String, content: AdContent) {
        if (content.hasNoItems()) {
            return
        }
        Handler(Looper.getMainLooper()).post {
            lock.lock()
            try {
                for (listener in listeners) {
                    listener.onContentAvailable(zoneId, content)
                }
            } finally {
                lock.unlock()
            }
        }
    }

    companion object {
        private val LOGTAG = AdContentPublisher::class.java.name
        private lateinit var instance: AdContentPublisher

        fun getInstance(): AdContentPublisher {
            if (!this::instance.isInitialized) {
                createInstance()
            }
            return instance
        }

        fun createInstance() {
            instance = AdContentPublisher()
        }
    }
}