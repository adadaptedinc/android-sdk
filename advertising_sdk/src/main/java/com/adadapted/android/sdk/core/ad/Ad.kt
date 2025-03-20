package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.core.payload.Payload
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Ad(
    val id: String = "",
    @SerialName("impression_id") val impressionId: String = "",
    @SerialName("creative_url") val url: String = "",
    @SerialName("action_type") val actionType: String = "",
    @SerialName("action_path") val actionPath: String? = "",
    val payload: Payload = Payload(),
) {

    val isEmpty: Boolean
        get() = id.isEmpty()

    fun getContent(): AdContent {
        return AdContent.createAddToListContent(this)
    }

    val zoneId: String
        get() = impressionId.split(":").first()
}