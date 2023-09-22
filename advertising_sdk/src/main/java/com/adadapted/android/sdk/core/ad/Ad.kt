package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.config.Config
import com.adadapted.android.sdk.ext.models.Payload
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Ad(
    @SerialName("ad_id") val id: String = "",
    @SerialName("impression_id") val impressionId: String = "",
    @SerialName("creative_url") val url: String = "",
    @SerialName("action_type") val actionType: String = "",
    @SerialName("action_path") val actionPath: String = "",
    val payload: Payload = Payload(),
    @SerialName("refresh_time") val refreshTime: Long = Config.DEFAULT_AD_REFRESH
) {
    private var isImpressionTracked: Boolean = false

    val isEmpty: Boolean
        get() = id.isEmpty()

    fun getContent(): AdContent {
        return AdContent.createAddToListContent(this)
    }

    fun resetImpressionTracking() {
        isImpressionTracked = false
    }

    fun setImpressionTracked() {
        isImpressionTracked = true
    }

    fun impressionWasTracked(): Boolean {
        return isImpressionTracked
    }

    val zoneId: String
        get() = impressionId.split(":").first()
}