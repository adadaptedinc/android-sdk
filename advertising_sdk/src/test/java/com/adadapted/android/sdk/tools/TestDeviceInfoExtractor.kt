package com.adadapted.android.sdk.tools

import android.content.Context
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.device.InfoExtractor

class TestDeviceInfoExtractor(var gaiaDisabled: Boolean = false): InfoExtractor {
    override fun extractDeviceInfo(context: Context, appId: String, isProd: Boolean, params: Map<String, String>): DeviceInfo {
        val mockDeviceInfo = DeviceInfo()
        mockDeviceInfo.appId = "TestAppId"
        mockDeviceInfo.device = "TestDevice"

        if (gaiaDisabled) {
            mockDeviceInfo.setAllowRetargeting(false)
        } else {
            mockDeviceInfo.setAllowRetargeting(true)
        }

        return mockDeviceInfo
    }
}