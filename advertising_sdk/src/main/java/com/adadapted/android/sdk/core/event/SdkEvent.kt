package com.adadapted.android.sdk.core.event

import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SdkEvent(
    @SerialName("event_source")
    val type: String,
    @SerialName("event_name")
    val name: String,
    @SerialName("event_timestamp")
    val timeStamp: Long = Clock.System.now().epochSeconds,
    @SerialName("event_params")
    val params: Map<String, String>
)
