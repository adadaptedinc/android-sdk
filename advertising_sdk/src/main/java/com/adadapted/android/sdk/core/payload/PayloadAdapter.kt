package com.adadapted.android.sdk.core.payload

import com.adadapted.android.sdk.core.atl.AdditContent
import com.adadapted.android.sdk.core.device.DeviceInfo

interface PayloadAdapter {
    suspend fun pickup(deviceInfo: DeviceInfo, callback: (content: List<AdditContent>) -> Unit)
    suspend fun publishEvent(deviceInfo: DeviceInfo, event: PayloadEvent)
}