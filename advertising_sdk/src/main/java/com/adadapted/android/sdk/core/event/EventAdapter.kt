package com.adadapted.android.sdk.core.event

import com.adadapted.android.sdk.core.device.DeviceInfo

interface EventAdapter {
    suspend fun publishAdEvents(sessionId: String, deviceInfo: DeviceInfo, adEvents: Set<AdEvent>)
    suspend fun publishSdkEvents(sessionId: String, deviceInfo: DeviceInfo, events: Set<SdkEvent>)
    suspend fun publishSdkErrors(sessionId: String, deviceInfo: DeviceInfo, errors: Set<SdkError>)
}