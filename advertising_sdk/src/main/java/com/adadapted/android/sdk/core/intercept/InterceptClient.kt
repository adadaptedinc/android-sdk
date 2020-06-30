package com.adadapted.android.sdk.core.intercept

import com.adadapted.android.sdk.core.concurrency.TransporterCoroutineScope
import com.adadapted.android.sdk.core.session.Session
import com.adadapted.android.sdk.core.session.SessionClient
import com.adadapted.android.sdk.core.session.SessionListener
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class InterceptClient private constructor(private val adapter: InterceptAdapter, private val transporter: TransporterCoroutineScope) : SessionListener() {
    interface Listener {
        fun onKeywordInterceptInitialized(intercept: Intercept)
    }

    private val events: MutableSet<InterceptEvent>
    private val eventLock: Lock = ReentrantLock()
    private lateinit var currentSession: Session

    private fun performInitialize(session: Session?, listener: Listener?) {
        if (session == null || listener == null) {
            return
        }
        adapter.retrieve(session, object : InterceptAdapter.Callback {
            override fun onSuccess(intercept: Intercept) {
                listener.onKeywordInterceptInitialized(intercept)
            }
        })
        SessionClient.getInstance().addListener(this)
    }

    private fun fileEvent(event: InterceptEvent) {
        eventLock.lock()
        try {
            val currentEvents: Set<InterceptEvent> = HashSet(events)
            events.clear()
            val resultingEvents = consolidateEvents(event, currentEvents)
            events.addAll(resultingEvents)
        } finally {
            eventLock.unlock()
        }
    }

    private fun consolidateEvents(event: InterceptEvent, events: Set<InterceptEvent>): Set<InterceptEvent> {
        val resultingEvents: MutableSet<InterceptEvent> = HashSet(this.events)
        // Create a new Set of Events not superseded by the current Event
        for (e in events) {
            if (!event.supersedes(e)) {
                resultingEvents.add(e)
            }
        }
        resultingEvents.add(event)
        return resultingEvents
    }

    private fun performPublishEvents() {
        eventLock.lock()
        try {
            if (events.isEmpty()) {
                return
            }
            val currentEvents: MutableSet<InterceptEvent> = HashSet(events)
            events.clear()
            adapter.sendEvents(currentSession, currentEvents)
        } finally {
            eventLock.unlock()
        }
    }

    override fun onSessionAvailable(session: Session) {
        eventLock.lock()
        currentSession = try {
            session
        } finally {
            eventLock.unlock()
        }
    }

    override fun onPublishEvents() {
        transporter.dispatchToBackground {
            performPublishEvents()
        }
    }

    @Synchronized
    fun initialize(session: Session?, listener: Listener?) {
        transporter.dispatchToBackground {
            performInitialize(session, listener)
        }
    }

    @Synchronized
    fun trackMatched(searchId: String,
                     termId: String,
                     term: String,
                     userInput: String) {
        trackEvent(searchId, termId, term, userInput, InterceptEvent.MATCHED)
    }

    @Synchronized
    fun trackPresented(searchId: String,
                       termId: String,
                       term: String,
                       userInput: String) {
        trackEvent(searchId, termId, term, userInput, InterceptEvent.PRESENTED)
    }

    @Synchronized
    fun trackSelected(searchId: String,
                      termId: String,
                      term: String,
                      userInput: String) {
        trackEvent(searchId, termId, term, userInput, InterceptEvent.SELECTED)
    }

    @Synchronized
    fun trackNotMatched(searchId: String, userInput: String) {
        trackEvent(searchId, "", "NA", userInput, InterceptEvent.NOT_MATCHED)
    }

    @Synchronized
    private fun trackEvent(searchId: String,
                           termId: String,
                           term: String,
                           userInput: String,
                           eventType: String) {

        val event = InterceptEvent(
                searchId,
                eventType,
                userInput,
                termId,
                term
        )

        transporter.dispatchToBackground {
            fileEvent(event)
        }
    }

    companion object {
        private val LOGTAG = InterceptClient::class.java.name
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
        SessionClient.getInstance().addListener(this)
    }
}