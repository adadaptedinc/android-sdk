package com.adadapted.android.sdk.core.event

import com.adadapted.android.sdk.core.device.DeviceInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventRequest( //TODO SERVER SIDE: this whole object should match up with device info on the backend
    @SerialName("session_id")
    val sessionId: String = "",
    @SerialName("app_id")
    val appId: String = "",
    @SerialName("bundle_id")
    val bundleId: String = "",
    @SerialName("bundle_version")
    val bundleVersion: String = "",
    val udid: String = "",
    val device: String = "",
    @SerialName("device_udid")
    val deviceUdid: String = "",
    val os: String = DeviceInfo.UNKNOWN_VALUE,
    val osv: String = "",
    val locale: String = "",
    val timezone: String = "",
    val carrier: String = "",
    val dw: Int = 0,
    val dh: Int = 0,
    val density: String = "",
    @SerialName("sdk_version")
    val sdkVersion: String = "",
    @SerialName("allow_retargeting")
    val isAllowRetargetingEnabled: Int = 0,
    val events: List<SdkEvent> = listOf(),
    val errors: List<SdkError> = listOf()
)
