package com.adadapted.android.sdk.core.device

import com.gitlab.adadapted.BuildConfig

class DeviceInfo {
    var appId: String? = null
    var isProd = false
    var scale = 0f
    var bundleId: String? = null
    var bundleVersion: String? = null
    var udid: String? = null
    var device: String? = null
    var deviceUdid: String? = null
    val os = OS
    var osv: String? = null
    var locale: String? = null
    var timezone: String? = null
    var carrier: String? = null
    var dw = 0
    var dh = 0
    var density = 0
    var isAllowRetargetingEnabled = false
        private set
    val sdkVersion: String = BuildConfig.VERSION_NAME
    var params: Map<String, String>? = null

    fun setAllowRetargeting(allowRetargeting: Boolean) {
        isAllowRetargetingEnabled = allowRetargeting
    }

    companion object {
        const val UNKNOWN_VALUE = "Unknown"
        const val OS = "Android"
        fun empty(): DeviceInfo {
            return DeviceInfo()
        }
    }
}