package com.adadapted.android.sdk.core.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class SdkEvent(
    @SerialName("event_source")
    val type: String,
    @SerialName("event_name")
    val name: String,
    @SerialName("event_timestamp")
    val timeStamp: Long = Date().time / 1000,
    @SerialName("event_params")
    val params: Map<String, String>
)
