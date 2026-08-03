package com.adadapted.android.sdk.core.event

import com.adadapted.android.sdk.core.concurrency.nowInSeconds
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SdkEvent(
    @SerialName("event_source")
    val type: String,
    @SerialName("event_name")
    val name: String,
    @SerialName("event_timestamp")
    val timeStamp: Long = nowInSeconds(),
    @SerialName("event_params")
    val params: Map<String, String>
)
