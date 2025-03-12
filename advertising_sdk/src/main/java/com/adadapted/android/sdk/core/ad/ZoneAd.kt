package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.core.payload.Payload
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ZoneAd(
    @SerialName("ad_id") val adId: String,
    @SerialName("impression_id") val impressionId: String,
    val type: String,
    @SerialName("port_height") val portHeight: Int,
    @SerialName("port_width") val portWidth: Int,
    @SerialName("creative_url") val creativeUrl: String,
    @SerialName("action_type") val actionType: String,
    @SerialName("action_path") val actionPath: String?,
    val payload: Payload = Payload()
)