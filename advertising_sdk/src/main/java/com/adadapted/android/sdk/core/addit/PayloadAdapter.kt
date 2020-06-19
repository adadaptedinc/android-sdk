package com.adadapted.android.sdk.core.addit

import com.adadapted.android.sdk.core.device.DeviceInfo

interface PayloadAdapter {
    interface Callback {
        fun onSuccess(content: List<AdditContent>)
    }

    fun pickup(deviceInfo: DeviceInfo, callback: Callback)
    fun publishEvent(event: PayloadEvent)
}