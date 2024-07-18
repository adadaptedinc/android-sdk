package com.adadapted.android.sdk.core.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class AdEvent(
    @SerialName("ad_id")
    val adId: String,
    val zoneId: String,
    @SerialName("impression_id")
    val impressionId: String,
    @SerialName("event_type")
    val eventType: String,
    @SerialName("created_at")
    val createdAt: Long = Date().time / 1000
)
