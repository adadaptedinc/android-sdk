package com.adadapted.android.sdk.tools

import android.content.Context

class TestDeviceInfoExtractor(var gaiaDisabled: Boolean = false): InfoExtractor {
    override fun extractDeviceInfo(context: Context, appId: String, isProd: Boolean, params: Map<String, String>, customIdentifier: String): DeviceInfo {
        val mockDeviceInfo = DeviceInfo()
        mockDeviceInfo.appId = "TestAppId"
        mockDeviceInfo.device = "TestDevice"
        mockDeviceInfo.udid = customIdentifier

        if (gaiaDisabled) {
            mockDeviceInfo.setAllowRetargeting(false)
        } else {
            mockDeviceInfo.setAllowRetargeting(true)
        }

        return mockDeviceInfo
    }
}