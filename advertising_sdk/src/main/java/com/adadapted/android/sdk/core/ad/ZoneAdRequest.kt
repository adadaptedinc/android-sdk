package com.adadapted.android.sdk.core.ad

import kotlinx.serialization.Serializable

@Serializable
data class ZoneAdRequest(
    val sdkId: String,
    val bundleId: String,
    val userId: String,
    val zoneId: String,
    val storeId: String,
    val contextId: String,
    val sessionId: String,
    val extra: String
)