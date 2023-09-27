package com.adadapted.android.sdk.tools

import com.adadapted.android.sdk.core.event.AdEvent
import com.adadapted.android.sdk.core.event.EventAdapter
import com.adadapted.android.sdk.core.event.SdkError
import com.adadapted.android.sdk.core.event.SdkEvent
import com.adadapted.android.sdk.core.session.Session

object TestEventAdapter: EventAdapter {
    var testAdEvents = mutableListOf<AdEvent>()
    var testSdkEvents = mutableListOf<SdkEvent>()
    var testSdkErrors = mutableListOf<SdkError>()

    override suspend fun publishAdEvents(session: Session, adEvents: Set<AdEvent>) {
        testAdEvents.addAll(adEvents)
    }

    override suspend fun publishSdkEvents(session: Session, events: Set<SdkEvent>) {
        testSdkEvents.addAll(events)
    }

    override suspend fun publishSdkErrors(session: Session, errors: Set<SdkError>) {
        testSdkErrors.addAll(errors)
    }

    fun cleanupEvents() {
        testAdEvents.clear()
        testSdkEvents.clear()
        testSdkErrors.clear()
    }
}