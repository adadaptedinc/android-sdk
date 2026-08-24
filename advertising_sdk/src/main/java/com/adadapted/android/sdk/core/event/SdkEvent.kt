package com.adadapted.android.sdk.core.event

import com.adadapted.android.sdk.core.concurrency.nowInSeconds
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class SdkEvent(
    @SerialName("event_source")
    val type: String,
    @SerialName("event_name")
    val name: String,
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    @SerialName("event_timestamp")
    val timeStamp: Long = nowInSeconds(),
    @SerialName("event_params")
    val params: Map<String, String>
)
