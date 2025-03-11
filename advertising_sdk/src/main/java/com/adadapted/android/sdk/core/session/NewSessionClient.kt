package com.adadapted.android.sdk.core.session

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.adadapted.android.sdk.constants.EventStrings
import com.adadapted.android.sdk.core.event.EventClient

class NewSessionClient: DefaultLifecycleObserver {
    private val prefix = "ANDROID"
    private var sessionId: String = ""
    private var backgroundTimer: Long = 0

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

    private fun generateId(): String = prefix + List(32) { ID_CHARACTERS.random() }.joinToString("")

    companion object {
        private val ID_CHARACTERS by lazy { ('A'..'Z') + ('0'..'9') }
        private const val THIRTY_MINUTES = 30 * 60 * 1000L
    }
}