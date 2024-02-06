package com.adadapted.android.sdk.core.keyword

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.interfaces.InterceptListener
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.core.session.SessionListener
import kotlin.jvm.Synchronized

class InterceptClient private constructor(
    private val adapter: InterceptAdapter,
    private val transporter: TransporterCoroutineScope
) : SessionListener {

    private val events: MutableSet<InterceptEvent>
    private lateinit var currentSession: Session

    private fun performInitialize(session: Session?, interceptListener: InterceptListener?) {
        if (session == null || interceptListener == null) {
            return
        }
        transporter.dispatchToThread {
            adapter.retrieve(session, object :
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
    private fun performPublishEvents() {
        if (events.isEmpty()) {
            return
        }
        val currentEvents: MutableSet<InterceptEvent> = HashSet(events)
        events.clear()
        transporter.dispatchToThread {
            adapter.sendEvents(currentSession, currentEvents)
        }
    }

    override fun onSessionAvailable(session: Session) {
        currentSession = session
    }

    override fun onPublishEvents() {
        transporter.dispatchToThread {
            performPublishEvents()
        }
    }

    fun initialize(session: Session?, interceptListener: InterceptListener?) {
        transporter.dispatchToThread {
            performInitialize(session, interceptListener)
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

    companion object {
        private lateinit var instance: InterceptClient

        fun getInstance(): InterceptClient {
            return instance
        }

        fun createInstance(adapter: InterceptAdapter, transporter: TransporterCoroutineScope) {
            instance = InterceptClient(adapter, transporter)
        }
    }

    init {
        events = HashSet()
        SessionClient.addListener(this)
    }
}