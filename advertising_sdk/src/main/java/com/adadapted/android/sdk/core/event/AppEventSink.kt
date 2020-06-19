package com.adadapted.android.sdk.core.event

import com.adadapted.android.sdk.core.device.DeviceInfo

interface AppEventSink {
    fun generateWrappers(deviceInfo: DeviceInfo)
    fun publishEvent(events: Set<AppEvent>)
    fun publishError(errors: Set<AppError>)
}