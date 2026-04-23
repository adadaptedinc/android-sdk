package com.adadapted.android.sdk.core.ad

import kotlinx.serialization.Serializable

@Serializable
data class AdZoneResponse(
    val data: AdZoneData = AdZoneData(),
    val success: Boolean = false
)
