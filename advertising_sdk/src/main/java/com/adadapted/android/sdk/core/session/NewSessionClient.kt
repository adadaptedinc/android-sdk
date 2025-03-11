package com.adadapted.android.sdk.core.session

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.concurrency.Transporter
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.event.EventClient
import com.adadapted.android.sdk.core.interfaces.SessionAdapter
import com.adadapted.android.sdk.core.interfaces.SessionAdapterListener

object NewSessionClient: DefaultLifecycleObserver, SessionAdapterListener {
    private val ID_CHARACTERS by lazy { ('A'..'Z') + ('0'..'9') }
    private const val THIRTY_MINUTES = 30 * 60 * 1000L
    private const val PREFIX = "ANDROID"
    private var adapter: SessionAdapter? = null //TODO change this to a new ZoneAd adadapter
    private var transporter: TransporterCoroutineScope = Transporter()
    private var sessionId: String = ""
    private var backgroundTimer: Long = 0
    private var hasInstance: Boolean = false
    private var sessionListeners: MutableSet<SessionListener> = HashSet()
    private var presenters: MutableSet<String> = HashSet()
    private var pollingTimerRunning: Boolean = false
    private var eventTimerRunning: Boolean = false

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        createOrResumeSession()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        sessionBackgrounded()
    }

    private fun createOrResumeSession() {
        val currentTime = System.currentTimeMillis()
        val isNewSession = sessionId.isEmpty() || (currentTime - backgroundTimer) >= THIRTY_MINUTES

        if (isNewSession) sessionId = generateId() else backgroundTimer = currentTime

        trackEvent(if (isNewSession) EventStrings.SESSION_CREATED else EventStrings.SESSION_RESUMED)
    }

    private fun sessionBackgrounded() {
        backgroundTimer = System.currentTimeMillis()
        trackEvent(EventStrings.SESSION_BACKGROUNDED)
    }

    private fun trackEvent(event: String) {
        EventClient.trackSdkEvent(event, mapOf("sessionId" to sessionId))
    }

    private fun generateId(): String = PREFIX + List(32) { ID_CHARACTERS.random() }.joinToString("")

    fun createInstance(adapter: SessionAdapter, transporter: TransporterCoroutineScope) {
        NewSessionClient.adapter = adapter
        NewSessionClient.transporter = transporter
        hasInstance = true
    }

    init {
        sessionListeners = HashSet()
        presenters = HashSet()
        pollingTimerRunning = false
        eventTimerRunning = false
    }

    override fun onSessionInitialized(session: Session) {
        TODO("Not yet implemented")
    }

    override fun onSessionInitializeFailed() {
        TODO("Not yet implemented")
    }

    override fun onNewAdsLoaded(session: Session) {
        TODO("Not yet implemented")
    }

    override fun onNewAdsLoadFailed() {
        TODO("Not yet implemented")
    }
}