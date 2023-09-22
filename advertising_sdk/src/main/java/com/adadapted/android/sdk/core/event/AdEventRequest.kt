package com.adadapted.android.sdk.core.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdEventRequest(
    @SerialName("session_id")
    val sessionId: String = "",
    @SerialName("app_id")
    val appId: String = "",
    val udid: String = "",
    @SerialName("sdk_version")
    val sdkVersion: String = "",
    val events: List<AdEvent> = listOf()
)
