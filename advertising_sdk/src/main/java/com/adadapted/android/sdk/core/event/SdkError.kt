package com.adadapted.android.sdk.core.event

import com.adadapted.android.sdk.core.concurrency.nowInSeconds
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SdkError(
    @SerialName("error_code")
    val code: String,
    @SerialName("error_message")
    val message: String,
    @SerialName("error_params")
    val params: Map<String, String>,
    @SerialName("error_timestamp")
    val timeStamp: Long = nowInSeconds()
)
