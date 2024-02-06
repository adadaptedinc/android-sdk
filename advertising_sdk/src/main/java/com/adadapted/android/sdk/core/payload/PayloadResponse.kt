package com.adadapted.android.sdk.core.payload

import kotlinx.serialization.Serializable

@Serializable
data class PayloadResponse(val payloads: List<Payload>)