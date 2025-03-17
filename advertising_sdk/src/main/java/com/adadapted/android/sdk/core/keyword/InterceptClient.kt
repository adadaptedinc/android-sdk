package com.adadapted.android.sdk.core.keyword

import com.adadapted.android.sdk.constants.Config
import com.adadapted.android.sdk.core.concurrency.Timer
import com.adadapted.android.sdk.core.concurrency.Transporter
import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.interfaces.InterceptListener
import com.adadapted.android.sdk.core.session.NewSessionClient
import kotlin.jvm.Synchronized

object InterceptClient {
    private lateinit var adapter: InterceptAdapter
    private var transporter: TransporterCoroutineScope = Transporter()
    private var interceptEventTimerRunning = false
    private val events: MutableSet<InterceptEvent> = HashSet()

    private fun performInitialize(sessionId: String, interceptListener: InterceptListener?) {
        if (interceptListener == null) {
            return
        }

        transporter.dispatchToThread {
            adapter.retrieve(sessionId, object :
                InterceptAdapter.Listener {
                override fun onSuccess(intercept: Intercept) {
                    interceptListener.onKeywordInterceptInitialized(intercept)
                }
            })
        }
    }

    @Synchronized
    private fun fileEvent(event: InterceptEvent) {
        val currentEvents: Set<InterceptEvent> = HashSet(events)
        events.clear()
        val resultingEvents = consolidateEvents(event, currentEvents)
        events.addAll(resultingEvents)
    }

    private fun consolidateEvents(
        event: InterceptEvent,
        events: Set<InterceptEvent>
    ): Set<InterceptEvent> {
        val resultingEvents: MutableSet<InterceptEvent> = HashSet(this.events)
        // Creates a new Set of Events not superseded by the current Event
        for (e in events) {
            if (!event.supersedes(e)) {
                resultingEvents.add(e)
            }
        }
        resultingEvents.add(event)
        return resultingEvents
    }

    @Synchronized
    private fun trackEvent(
        searchId: String,
        termId: String,
        term: String,
        userInput: String,
        eventType: String
    ) {

        val event = InterceptEvent(
            searchId,
            eventType,
            userInput,
            termId,
            term
        )

        transporter.dispatchToThread {
            fileEvent(event)
        }
    }

    @Synchronized
    private fun performPublishEvents() {
        if (events.isEmpty()) {
            return
        }
        val currentEvents: MutableSet<InterceptEvent> = HashSet(events)
        events.clear()
        transporter.dispatchToThread {
            adapter.sendEvents(NewSessionClient.getSessionId(), currentEvents)
        }
    }

    private fun startPublishTimer() {
        if (interceptEventTimerRunning) {
            return
        }
        interceptEventTimerRunning = true

        val eventTimer = Timer(
            { performPublishEvents() },
            repeatMillis = Config.DEFAULT_EVENT_POLLING,
            delayMillis = Config.DEFAULT_EVENT_POLLING
        )
        eventTimer.startTimer()
    }

    fun initialize(sessionId: String, interceptListener: InterceptListener?) {
        transporter.dispatchToThread {
            performInitialize(sessionId, interceptListener)
        }
    }

    @Synchronized
    fun trackMatched(
        searchId: String,
        termId: String,
        term: String,
        userInput: String
    ) {
        trackEvent(searchId, termId, term, userInput, InterceptEvent.MATCHED)
    }

    @Synchronized
    fun trackPresented(
        searchId: String,
        termId: String,
        term: String,
        userInput: String
    ) {
        trackEvent(searchId, termId, term, userInput, InterceptEvent.PRESENTED)
    }

    @Synchronized
    fun trackSelected(
        searchId: String,
        termId: String,
        term: String,
        userInput: String
    ) {
        trackEvent(searchId, termId, term, userInput, InterceptEvent.SELECTED)
    }

    @Synchronized
    fun trackNotMatched(searchId: String, userInput: String) {
        trackEvent(searchId, "", "NA", userInput, InterceptEvent.NOT_MATCHED)
    }

    fun createInstance(adapter: InterceptAdapter, transporter: TransporterCoroutineScope, isKeywordInterceptEnabled: Boolean) {
        InterceptClient.adapter = adapter
        InterceptClient.transporter = transporter
        if (isKeywordInterceptEnabled) {
            startPublishTimer()
        }
    }
}