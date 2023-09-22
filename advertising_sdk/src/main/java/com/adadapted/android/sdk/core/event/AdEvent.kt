package com.adadapted.android.sdk.core.event

import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val createdAt: Long = Clock.System.now().epochSeconds
)
