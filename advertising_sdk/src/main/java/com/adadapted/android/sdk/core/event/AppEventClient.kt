package com.adadapted.android.sdk.core.event

import android.util.Log
import com.adadapted.android.sdk.config.EventStrings
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.core.session.SessionListener
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class AppEventClient private constructor(private val sink: AppEventSink, private val transporter: TransporterCoroutineScope): SessionListener() {
    private object Types {
        const val SDK = "sdk"
        const val APP = "app"
    }

    private val events: MutableSet<AppEvent>
    private val eventLock: Lock = ReentrantLock()
    private val errors: MutableSet<AppError>
    private val errorLock: Lock = ReentrantLock()
    private fun performTrackEvent(type: String,
                                  name: String,
                                  params: Map<String, String>) {
        eventLock.lock()
        try {
            events.add(AppEvent(type, name, params))
        } finally {
            eventLock.unlock()
        }
    }

    private fun performTrackError(code: String,
                                  message: String,
                                  params: Map<String, String>) {
        Log.w(LOGTAG, "App Error: $code - $message")
        errorLock.lock()
        try {
            errors.add(AppError(code, message, params))
        } finally {
            errorLock.unlock()
        }
    }

    private fun performPublishEvents() {
        eventLock.lock()
        try {
            if (events.isNotEmpty()) {
                val currentEvents: Set<AppEvent> = HashSet(events)
                events.clear()
                sink.publishEvent(currentEvents)
            }
        } finally {
            eventLock.unlock()
        }
    }

    private fun performPublishErrors() {
        errorLock.lock()
        try {
            if (errors.isNotEmpty()) {
                val currentErrors: Set<AppError> = HashSet(errors)
                errors.clear()
                sink.publishError(currentErrors)
            }
        } finally {
            errorLock.unlock()
        }
    }

    @Synchronized
    fun trackSdkEvent(name: String, params: Map<String, String>) {
        transporter.dispatchToBackground { performTrackEvent(Types.SDK, name, params) }
    }

    @Synchronized
    fun trackSdkEvent(name: String) {
        trackSdkEvent(name, HashMap())
    }

    @Synchronized
    fun trackError(code: String, message: String, params: Map<String, String>) {
        transporter.dispatchToBackground {
            instance.performTrackError(code, message, params)
        }
    }

    @Synchronized
    fun trackError(code: String, message: String) {
        trackError(code, message, HashMap())
    }

    @Synchronized
    fun trackAppEvent(name: String, params: Map<String, String>) {
        instance.transporter.dispatchToBackground {
            instance.performTrackEvent(Types.APP, name, params)
        }
    }

    @Synchronized
    fun trackAppEvent(name: String) {
        trackAppEvent(name, HashMap())
    }

    companion object {
        private val LOGTAG = AppEventClient::class.java.name
        private lateinit var instance: AppEventClient

        fun createInstance(sink: AppEventSink, transporter: TransporterCoroutineScope) {
            instance = AppEventClient(sink, transporter)
        }

        fun getInstance(): AppEventClient {
            return instance
        }
    }

    init {
        events = HashSet()
        errors = HashSet()

        DeviceInfoClient.getInstance().getDeviceInfo(object: DeviceInfoClient.Callback {
            override fun onDeviceInfoCollected(deviceInfo: DeviceInfo) {
                sink.generateWrappers(deviceInfo)
            }
        })

        SessionClient.getInstance().addListener(this)
    }

    override fun onPublishEvents() {
        instance.transporter.dispatchToBackground {
            instance.performPublishEvents()
            instance.performPublishErrors()
        }
    }

    override fun onSessionExpired() {
        trackSdkEvent(EventStrings.EXPIRED_EVENT)
    }
}