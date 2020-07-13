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
    private fun performTrackEvent(type: String, name: String, params: Map<String, String>) {
        eventLock.lock()
        try {
            events.add(AppEvent(type, name, params))
        } finally {
            eventLock.unlock()
        }
    }

    private fun performTrackError(code: String, message: String, params: Map<String, String>) {
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

    override fun onPublishEvents() {
        transporter.dispatchToBackground {
            performPublishEvents()
            performPublishErrors()
        }
    }

    override fun onSessionExpired() {
        trackSdkEvent(EventStrings.EXPIRED_EVENT)
    }

    @Synchronized
    fun trackSdkEvent(name: String, params: Map<String, String> = HashMap()) {
        transporter.dispatchToBackground { performTrackEvent(Types.SDK, name, params) }
    }

    @Synchronized
    fun trackError(code: String, message: String, params: Map<String, String> = HashMap()) {
        transporter.dispatchToBackground {
            performTrackError(code, message, params)
        }
    }

    @Synchronized
    fun trackAppEvent(name: String, params: Map<String, String> = HashMap()) {
        transporter.dispatchToBackground {
            performTrackEvent(Types.APP, name, params)
        }
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
}