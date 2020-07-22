package com.adadapted.android.sdk.core.device

import android.content.Context

interface InfoExtractor {
    fun extractDeviceInfo(context: Context, appId: String, isProd: Boolean, params: Map<String, String>) : DeviceInfo
}