package com.adadapted.android.sdk.core.session

import com.adadapted.android.sdk.constants.Config
import com.adadapted.android.sdk.core.concurrency.Timer
import com.adadapted.android.sdk.core.concurrency.Transporter
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.DeviceInfoClient
import com.adadapted.android.sdk.core.interfaces.DeviceCallback
import com.adadapted.android.sdk.core.interfaces.SessionAdapter
import com.adadapted.android.sdk.core.interfaces.SessionAdapterListener
import com.adadapted.android.sdk.core.log.AALogger
import com.adadapted.android.sdk.core.view.ZoneContext
import kotlin.jvm.Synchronized

object SessionClient : SessionAdapterListener {

    enum class Status {
        OK,  // Normal Status. No alterations to regular behavior
        SHOULD_REFRESH,  // SDK should refresh Ads or Reinitialize Session at the next available chance
        IS_REFRESH_ADS,  // SDK is currently refreshing Ads
        IS_REINITIALIZING_SESSION // SDK is currently reinitializing the Session
    }

    private lateinit var currentSession: Session
    private var adapter: SessionAdapter? = null
    private var transporter: TransporterCoroutineScope = Transporter()
    private val sessionListeners: MutableSet<SessionListener>
    private val presenters: MutableSet<String>
    var status: Status
        private set
    private var pollingTimerRunning: Boolean
    private var eventTimerRunning: Boolean
    private var hasInstance: Boolean = false
    private var zoneContexts: MutableSet<ZoneContext> = mutableSetOf()

    private fun performAddListener(listener: SessionListener) {
        sessionListeners.add(listener)
        if (this::currentSession.isInitialized) {
            listener.onSessionAvailable(currentSession)
        }
    }

    private fun performRemoveListener(listener: SessionListener) {
        sessionListeners.remove(listener)
    }

    private fun performAddPresenter(listener: SessionListener) {
        performAddListener(listener)
        presenters.add(listener.toString())

        if (status == Status.SHOULD_REFRESH) {
            performRefresh()
        }
    }

    private fun performRemovePresenter(listener: SessionListener) {
        performRemoveListener(listener)
        presenters.remove(listener.toString())
    }

    private fun presenterSize() = presenters.size

    private fun performInitialize(deviceInfo: DeviceInfo) {
        transporter.dispatchToThread { adapter?.sendInit(deviceInfo, this@SessionClient) }
    }

    private fun performRefresh(
        deviceInfo: DeviceInfo? = DeviceInfoClient.getCachedDeviceInfo()
    ) {
        if (currentSession.hasExpired()) {
            AALogger.logInfo("Session has expired. Expired at: " + currentSession.expiration)
            notifySessionExpired()
            if (deviceInfo != null) {
                performReinitialize(deviceInfo)
            }
        } else {
            performRefreshAds()
        }
    }

    private fun performReinitialize(deviceInfo: DeviceInfo) {
        if (status == Status.OK || status == Status.SHOULD_REFRESH) {
            if (presenterSize() > 0) {
                AALogger.logInfo("Reinitializing Session.")
                status = Status.IS_REINITIALIZING_SESSION
                transporter.dispatchToThread {
                    adapter?.sendInit(deviceInfo, this@SessionClient)
                }
            } else {
                status = Status.SHOULD_REFRESH
            }
        }
    }

    private fun performRefreshAds() {
        if (status == Status.OK || status == Status.SHOULD_REFRESH) {
            if (presenterSize() > 0) {
                AALogger.logInfo("Checking for more Ads.")
                status = if(zoneContexts.any()) { Status.OK } else { Status.IS_REFRESH_ADS }
                transporter.dispatchToThread {
                    adapter?.sendRefreshAds(
                        currentSession,
                        this@SessionClient,
                        zoneContexts
                    )
                }
            } else {
                status = Status.SHOULD_REFRESH
            }
        }
    }

    private fun updateCurrentSession(session: Session) {
        currentSession = session
        startPublishTimer()
        startPollingTimer()
    }

    private fun updateCurrentZones(session: Session) {
        currentSession.updateZones(session.getAllZones())
        startPollingTimer()
    }

    private fun startPollingTimer() {
        if (pollingTimerRunning || currentSession.willNotServeAds()) {
            AALogger.logInfo("Session will not serve Ads. Ignoring Ad polling timer.")
            return
        }
        pollingTimerRunning = true
        AALogger.logInfo("Starting Ad polling timer.")

        val refreshTimer = Timer(
            { performRefresh() },
            repeatMillis = currentSession.refreshTime,
            delayMillis = currentSession.refreshTime
        )
        refreshTimer.startTimer()
    }

    private fun startPublishTimer() {
        if (eventTimerRunning) {
            return
        }
        eventTimerRunning = true

        val eventTimer = Timer(
            { notifyPublishEvents() },
            repeatMillis = Config.DEFAULT_EVENT_POLLING,
            delayMillis = Config.DEFAULT_EVENT_POLLING
        )
        eventTimer.startTimer()
    }

    private fun notifyPublishEvents() {
        for (l in sessionListeners) {
            l.onPublishEvents()
        }
    }

    private fun notifySessionAvailable() {
        for (l in sessionListeners) {
            currentSession.let { l.onSessionAvailable(it) }
        }
    }

    private fun notifyAdsAvailable() {
        for (l in sessionListeners) {
            currentSession.let { l.onAdsAvailable(it) }
        }
    }

    private fun notifySessionInitFailed() {
        for (l in sessionListeners) {
            l.onSessionInitFailed()
        }
    }

    private fun notifySessionExpired() {
        for (l in sessionListeners) {
            l.onSessionExpired()
        }
    }

    override fun onSessionInitialized(session: Session) {
        updateCurrentSession(session)
        status = Status.OK
        notifySessionAvailable()
    }

    override fun onSessionInitializeFailed() {
        updateCurrentSession(Session())
        status = Status.OK
        notifySessionInitFailed()
    }

    override fun onNewAdsLoaded(session: Session) {
        updateCurrentZones(session)
        status = Status.OK
        notifyAdsAvailable()
    }

    override fun onNewAdsLoadFailed() {
        updateCurrentZones(Session())
        status = Status.OK
        notifyAdsAvailable()
    }

    @Synchronized
    fun start(listener: SessionListener) {
        addListener(listener)
        DeviceInfoClient.getDeviceInfo(object : DeviceCallback {
            override fun onDeviceInfoCollected(deviceInfo: DeviceInfo) {
                transporter.dispatchToThread {
                    performInitialize(deviceInfo)
                }
            }
        })
    }

    fun addListener(listener: SessionListener) {
        performAddListener(listener)
    }

    fun removeListener(listener: SessionListener) {
        performRemoveListener(listener)
    }

    fun addPresenter(listener: SessionListener) {
        performAddPresenter(listener)
    }

    fun removePresenter(listener: SessionListener) {
        performRemovePresenter(listener)
    }

    fun hasStaleAds(): Boolean {
        return status != Status.OK
    }

    fun hasInstance(): Boolean {
        return hasInstance
    }

    fun setZoneContext(zoneContext: ZoneContext) {
        val existingContext = zoneContexts.find { it.zoneId == zoneContext.zoneId }
        if (existingContext != null) {
            zoneContexts.remove(existingContext)
        }
        zoneContexts.add(zoneContext)
        performRefreshAds()
    }

    fun removeZoneContext(zoneId: String) {
        val wasRemoved = zoneContexts.removeAll { z -> z.zoneId == zoneId }
        if (wasRemoved) {
            performRefreshAds()
        }
    }

    fun clearZoneContext(){
        zoneContexts = mutableSetOf()
        performRefreshAds()
    }

    fun createInstance(adapter: SessionAdapter, transporter: TransporterCoroutineScope) {
        SessionClient.adapter = adapter
        SessionClient.transporter = transporter
        hasInstance = true
    }

    init {
        sessionListeners = HashSet()
        presenters = HashSet()
        pollingTimerRunning = false
        eventTimerRunning = false
        status = Status.OK
    }
}