package com.adadapted.android.sdk.core.keyword

import kotlinx.serialization.Serializable

@Serializable
data class KeywordRequest(
    val sdkId: String,
    val bundleId: String,
    val userId: String,
    val zoneId: String,
    val sessionId: String,
    val extra: String
)