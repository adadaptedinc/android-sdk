package com.adadapted.android.sdk.core.session

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.event.EventClient

object SessionClient: DefaultLifecycleObserver {
    private const val PREFIX = "ANDROID"
    private const val THIRTY_MINUTES = 30 * 60 * 1000L
    private val ID_CHARACTERS by lazy { ('A'..'Z') + ('0'..'9') }
    private var sessionId: String = ""
    private var backgroundTime: Long = 0

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        createOrResumeSession()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        sessionBackgrounded()
    }

    fun getSessionId(): String {
        return sessionId
    }

    @Synchronized
    private fun createOrResumeSession() {
        val currentTime = System.currentTimeMillis()
        val isNewSession = sessionId.isEmpty() || (currentTime - backgroundTime) >= THIRTY_MINUTES

        if (isNewSession) sessionId = generateId() else backgroundTime = currentTime

        trackEvent(if (isNewSession) EventStrings.SESSION_CREATED else EventStrings.SESSION_RESUMED)
    }

    private fun sessionBackgrounded() {
        backgroundTime = System.currentTimeMillis()
        trackEvent(EventStrings.SESSION_BACKGROUNDED)
    }

    private fun trackEvent(event: String) {
        EventClient.trackSdkEvent(event, mapOf("sessionId" to sessionId))
    }

    private fun generateId(): String = PREFIX + List(32) { ID_CHARACTERS.random() }.joinToString("")
}