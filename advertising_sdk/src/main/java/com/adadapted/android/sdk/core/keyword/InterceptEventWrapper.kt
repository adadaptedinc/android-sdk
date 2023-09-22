package com.adadapted.android.sdk.core.keyword

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InterceptEventWrapper(
    @SerialName("session_id")
    val sessionId: String,
    @SerialName("app_id")
    val appId: String,
    val udid: String,
    @SerialName("sdk_version")
    val sdkVersion: String,
    val events: Set<InterceptEvent>
)