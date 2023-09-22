package com.adadapted.android.sdk.core.payload

import com.adadapted.android.sdk.core.atl.AddItContent
import com.adadapted.android.sdk.core.device.DeviceInfo

interface PayloadAdapter {
    suspend fun pickup(deviceInfo: DeviceInfo, callback: (content: List<AddItContent>) -> Unit)
    suspend fun publishEvent(deviceInfo: DeviceInfo, event: PayloadEvent)
}