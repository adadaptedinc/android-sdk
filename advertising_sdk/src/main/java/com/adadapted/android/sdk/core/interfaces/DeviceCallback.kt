package com.adadapted.android.sdk.core.interfaces

import com.adadapted.android.sdk.core.device.DeviceInfo

interface DeviceCallback {
    fun onDeviceInfoCollected(deviceInfo: DeviceInfo)
}