package com.adadapted.android.sdk.core.session

import android.os.Handler
import android.util.Log
import com.adadapted.android.sdk.config.Config
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.HashSet

class SessionClient private constructor(private val adapter: SessionAdapter, private val transporter: TransporterCoroutineScope) : SessionAdapter.Listener {

    internal enum class Status {
        OK,  // Normal Status. No alterations to regular behavior
        SHOULD_REFRESH,  // SDK should refresh Ads or Reinitialize Session at the next available chance
        IS_REFRESH_ADS,  // SDK is currently refreshing Ads
        IS_REINITIALIZING_SESSION // SDK is currently reinitializing the Session
    }

    private val listeners: MutableSet<SessionListener>
    private val listenerLock: Lock = ReentrantLock()
    private val presenters: MutableSet<String>
    private val presenterLock: Lock = ReentrantLock()
    private lateinit var deviceInfo: DeviceInfo
    private val deviceInfoLock: Lock = ReentrantLock()
    private lateinit var currentSession: Session
    private val sessionLock: Lock = ReentrantLock()
    private var status: Status
    private val statusLock: Lock = ReentrantLock()
    private var pollingTimerRunning: Boolean
    private var eventTimerRunning: Boolean

    private fun getStatus(): Status {
        statusLock.lock()
        return try {
            status
        } finally {
            statusLock.unlock()
        }
    }

    private fun setStatus(status: Status) {
        statusLock.lock()
        try {
            this.status = status
        } finally {
            statusLock.unlock()
        }
    }

    private fun performAddListener(listener: SessionListener) {
        listenerLock.lock()
        try {
            listeners.add(listener)
        } finally {
            listenerLock.unlock()
        }
        if (this::currentSession.isInitialized) {
            listener.onSessionAvailable(currentSession)
        }
    }

    private fun performRemoveListener(listener: SessionListener) {
        listenerLock.lock()
        try {
            listeners.remove(listener)
        } finally {
            listenerLock.unlock()
        }
    }

    private fun performAddPresenter(listener: SessionListener) {
        performAddListener(listener)
        presenterLock.lock()
        try {
            presenters.add(listener.toString())
        } finally {
            presenterLock.unlock()
        }
        if (getStatus() == Status.SHOULD_REFRESH) {
            performRefresh()
        }
    }

    private fun performRemovePresenter(listener: SessionListener) {
        performRemoveListener(listener)
        presenterLock.lock()
        try {
            presenters.remove(listener.toString())
        } finally {
            presenterLock.unlock()
        }
    }

    private fun presenterSize(): Int {
        presenterLock.lock()
        return try {
            presenters.size
        } finally {
            presenterLock.unlock()
        }
    }

    private fun performInitialize(deviceInfo: DeviceInfo) {
        deviceInfoLock.lock()
        try {
            this.deviceInfo = deviceInfo
        } finally {
            deviceInfoLock.unlock()
        }
        adapter.sendInit(deviceInfo, this)
    }

    private fun performRefresh() {
        sessionLock.lock()
        try {
            if (currentSession.hasExpired()) {
                Log.i(LOGTAG, "Session has expired. Expired at: " + currentSession.expiresAt())
                notifySessionExpired()
                performReinitialize()
            } else {
                performRefreshAds()
            }
        } finally {
            sessionLock.unlock()
        }
    }

    private fun performReinitialize() {
        if (getStatus() == Status.OK || getStatus() == Status.SHOULD_REFRESH) {
            if (presenterSize() > 0) {
                Log.i(LOGTAG, "Reinitializing Session.")
                deviceInfoLock.lock()
                try {
                    setStatus(Status.IS_REINITIALIZING_SESSION)
                    adapter.sendInit(deviceInfo, this)
                } finally {
                    deviceInfoLock.unlock()
                }
            } else {
                setStatus(Status.SHOULD_REFRESH)
            }
        }
    }

    private fun performRefreshAds() {
        if (getStatus() == Status.OK || getStatus() == Status.SHOULD_REFRESH) {
            if (presenterSize() > 0) {
                Log.i(LOGTAG, "Checking for more Ads.")
                sessionLock.lock()
                try {
                    setStatus(Status.IS_REFRESH_ADS)
                    adapter.sendRefreshAds(currentSession, this)
                } finally {
                    sessionLock.unlock()
                }
            } else {
                setStatus(Status.SHOULD_REFRESH)
            }
        }
    }

    private fun updateCurrentSession(session: Session) {
        sessionLock.lock()
        try {
            currentSession = session
            startPublishTimer()
        } finally {
            sessionLock.unlock()
        }
        startPollingTimer()
    }

    private fun updateCurrentZones(session: Session) {
        sessionLock.lock()
        currentSession = try {
            session
        } finally {
            sessionLock.unlock()
        }
        startPollingTimer()
    }

    private fun startPollingTimer() {
        if (pollingTimerRunning || currentSession.willNotServeAds()) {
            Log.i(LOGTAG, "Session will not serve Ads. Ignoring Ad polling timer.")
            return
        }
        pollingTimerRunning = true
        sessionLock.lock()
        try {
            Log.i(LOGTAG, "Starting Ad polling timer.")
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    pollingTimerRunning = false
                    performRefresh()
                }
            }, currentSession.refreshTime)
        } finally {
            sessionLock.unlock()
        }
    }

    private fun startPublishTimer() {
        if(eventTimerRunning) {
            return
        }

        Log.i(LOGTAG, "Starting up the Event Publisher.")
        eventTimerRunning = true

        val handler = Handler()
        val publisher: Runnable = object : Runnable{
            override fun run() {
                notifyPublishEvents()
                handler.postDelayed(this, Config.DEFAULT_EVENT_POLLING)
            }
        }

        publisher.run()
    }

    private fun notifyPublishEvents() {
        listenerLock.lock()
        try {
            for (l in listeners) {
                l.onPublishEvents()
            }
        } finally {
            listenerLock.unlock()
        }
    }

    private fun notifySessionAvailable() {
        listenerLock.lock()
        try {
            for (l in listeners) {
                l.onSessionAvailable(currentSession)
            }
        } finally {
            listenerLock.unlock()
        }
    }

    private fun notifyAdsAvailable() {
        listenerLock.lock()
        try {
            for (l in listeners) {
                l.onAdsAvailable(currentSession)
            }
        } finally {
            listenerLock.unlock()
        }
    }

    private fun notifySessionInitFailed() {
        listenerLock.lock()
        try {
            for (l in listeners) {
                l.onSessionInitFailed()
            }
        } finally {
            listenerLock.unlock()
        }
    }

    private fun notifySessionExpired() {
        listenerLock.lock()
        try {
            for (l in listeners) {
                l.onSessionExpired()
            }
        } finally {
            listenerLock.unlock()
        }
    }

    override fun onSessionInitialized(session: Session) {
        updateCurrentSession(session)
        notifySessionAvailable()
        setStatus(Status.OK)
    }

    override fun onSessionInitializeFailed() {
        updateCurrentSession(Session().apply { setDeviceInfo(deviceInfo) })
        notifySessionInitFailed()
        setStatus(Status.OK)
    }

    override fun onNewAdsLoaded(session: Session) {
        updateCurrentZones(session)
        notifyAdsAvailable()
        setStatus(Status.OK)
    }

    override fun onNewAdsLoadFailed() {
        updateCurrentZones(Session().apply { setDeviceInfo(deviceInfo) })
        notifyAdsAvailable()
        setStatus(Status.OK)
    }

    @Synchronized
    fun start(listener: SessionListener) {
        addListener(listener)
        DeviceInfoClient.getInstance().getDeviceInfo(object: DeviceInfoClient.Callback {
            override fun onDeviceInfoCollected(deviceInfo: DeviceInfo) {
                transporter.dispatchToBackground {
                    performInitialize(deviceInfo)
                }
            }
        })
    }

    fun addListener(listener: SessionListener) {
        transporter.dispatchToBackground {
            performAddListener(listener)
        }
    }

    fun removeListener(listener: SessionListener) {
        transporter.dispatchToBackground {
            performRemoveListener(listener)
        }
    }

    fun addPresenter(listener: SessionListener) {
        transporter.dispatchToBackground {
            performAddPresenter(listener)
        }
    }

    fun removePresenter(listener: SessionListener) {
        transporter.dispatchToBackground {
            performRemovePresenter(listener)
        }
    }

    companion object {
        private val LOGTAG = SessionClient::class.java.name
        private lateinit var instance: SessionClient

        fun getInstance(): SessionClient {
            return instance
        }

        fun createInstance(adapter: SessionAdapter, transporter: TransporterCoroutineScope) {
            instance = SessionClient(adapter, transporter)
        }

        fun hasInstance(): Boolean {
            return this::instance.isInitialized
        }
    }

    init {
        listeners = HashSet()
        presenters = HashSet()
        pollingTimerRunning = false
        eventTimerRunning = false
        status = Status.OK
    }
}