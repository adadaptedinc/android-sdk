package com.adadapted.android.sdk.core.ad

import kotlinx.serialization.Serializable

@Serializable
data class AdResponse(
    val data: AdZoneData,
    val success: Boolean
)