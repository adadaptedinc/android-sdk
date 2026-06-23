package com.adadapted.android.sdk.core.keyword

import kotlinx.serialization.Serializable

@Serializable
data class InterceptResponse(
    val data: InterceptData = InterceptData(),
    val success: Boolean = false
)
