package com.adadapted.android.sdk.core.device

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class DeviceInfo(
    @SerialName("app_id")val appId: String = UNKNOWN_VALUE,
    val isProd: Boolean = false,
    val customIdentifier: String = "",
    val scale: Float = 0f,
    @SerialName("bundle_id") val bundleId: String = "",
    @SerialName("bundle_version") val bundleVersion: String = "",
    val udid: String = "",
    @SerialName("device_name") val deviceName: String = "",
    @SerialName("device_udid") val deviceUdid: String = "",
    @SerialName("device_os") val os: String = UNKNOWN_VALUE,
    @SerialName("device_osv") val osv: String = "",
    @SerialName("device_locale") val locale: String = "",
    @SerialName("device_timezone") val timezone: String = "",
    @SerialName("device_carrier") val carrier: String = "",
    @SerialName("device_width") val dw: Int = 0,
    @SerialName("device_height") val dh: Int = 0,
    @SerialName("device_density") val density: String = "",
    @SerialName("allow_retargeting") val isAllowRetargetingEnabled: Boolean = false,
    @SerialName("sdk_version") val sdkVersion: String = "",
    @SerialName("created_at") val createdAt: Long = 0,
    val params: Map<String, String> = mapOf()
) {

    companion object {
        const val UNKNOWN_VALUE = "Unknown"
    }
}