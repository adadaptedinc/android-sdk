package com.adadapted.android.sdk.tools

import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.DeviceInfoExtractor
import com.nhaarman.mockitokotlin2.mock

class TestDeviceInfoExtractor: DeviceInfoExtractor(mock()) {
    override fun extractDeviceInfo(
        appId: String,
        isProd: Boolean,
        customIdentifier: String,
        params: Map<String, String>
    ): DeviceInfo {
        return DeviceInfo(deviceName = "TestDevice", udid = "customUDID")
    }
}