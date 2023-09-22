package com.adadapted.android.sdk.tools

open class TestAdEventSink: AdEventSink {
    var testEvents = setOf<AdEvent>()

    override fun sendBatch(session: Session, events: Set<AdEvent>) {
        testEvents = events
    }
}