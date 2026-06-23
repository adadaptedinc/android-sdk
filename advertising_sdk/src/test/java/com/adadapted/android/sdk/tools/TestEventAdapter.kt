package com.adadapted.android.sdk.tools

import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.event.AdEvent
import com.adadapted.android.sdk.core.event.EventAdapter
import com.adadapted.android.sdk.core.event.SdkError
import com.adadapted.android.sdk.core.event.SdkEvent

object TestEventAdapter: EventAdapter {
    var testAdEvents = mutableListOf<AdEvent>()
    var testSdkEvents = mutableListOf<SdkEvent>()
    var testSdkErrors = mutableListOf<SdkError>()

    fun cleanupEvents() {
        testAdEvents.clear()
        testSdkEvents.clear()
        testSdkErrors.clear()
    }

    override suspend fun publishAdEvents(
        sessionId: String,
        deviceInfo: DeviceInfo,
        adEvents: Set<AdEvent>
    ) {
        testAdEvents.addAll(adEvents)
    }

    override suspend fun publishSdkEvents(
        sessionId: String,
        deviceInfo: DeviceInfo,
        events: Set<SdkEvent>
    ) {
        testSdkEvents.addAll(events)
    }

    override suspend fun publishSdkErrors(
        sessionId: String,
        deviceInfo: DeviceInfo,
        errors: Set<SdkError>
    ) {
        testSdkErrors.addAll(errors)
    }
}