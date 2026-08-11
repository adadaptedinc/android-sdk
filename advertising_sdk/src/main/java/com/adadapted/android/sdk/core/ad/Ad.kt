package com.adadapted.android.sdk.core.ad

import com.adadapted.android.sdk.constants.Config
import com.adadapted.android.sdk.core.payload.Payload
import kotlinx.serialization.SerialName
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.contentOrNull

@kotlinx.serialization.Serializable
data class Ad(
    @SerialName("id") val id: String = "",
    @SerialName("impression_id") val impressionId: String = "",
    @SerialName("creative_url") val url: String = "",
    @SerialName("action_type") val actionType: String = "",
    @SerialName("action_path") val actionPath: String = "",
    val payload: Payload = Payload(),
    @SerialName("refresh_time")
    @kotlinx.serialization.Serializable(with = RefreshTimeSerializer::class)
    val refreshTime: Long = NO_REFRESH_TIME
) {
    private var isImpressionTracked: Boolean = false
    private var isImpressionEndTracked: Boolean = false

    val isEmpty: Boolean
        get() = id.isEmpty()

    val refreshTimeOrDefault: Long
        get() = if (refreshTime <= NO_REFRESH_TIME) {
            Config.DEFAULT_AD_REFRESH_SECONDS
        } else {
            refreshTime.coerceAtLeast(MINIMUM_REFRESH_TIME_SECONDS)
        }

    /** True when the server sent a [refreshTime] the SDK did not use verbatim. Absent does not count. */
    val refreshTimeWasRejected: Boolean
        get() = refreshTime != NO_REFRESH_TIME && refreshTime != refreshTimeOrDefault

    fun getContent(): AdContent {
        return AdContent.createAddToListContent(this)
    }

    fun setImpressionTracked() {
        isImpressionTracked = true
    }

    fun impressionWasTracked(): Boolean {
        return isImpressionTracked
    }

    fun setImpressionEndTracked() {
        isImpressionEndTracked = true
    }

    fun impressionEndWasTracked(): Boolean {
        return isImpressionEndTracked
    }

    val zoneId: String
        get() = impressionId.split(":").first()

    companion object {
        internal const val NO_REFRESH_TIME = 0L
        internal const val MINIMUM_REFRESH_TIME_SECONDS = 15L
    }
}

/**
 * Decodes anything that is not a whole number of seconds - null, an empty string, text, an object -
 * to [Ad.NO_REFRESH_TIME] so an unusable "refresh_time" cannot fail the whole Ad response.
 */
internal object RefreshTimeSerializer : JsonTransformingSerializer<Long>(Long.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement {
        val refreshTime = (element as? JsonPrimitive)?.contentOrNull
            ?.let { it.toLongOrNull() ?: it.toDoubleOrNull()?.toLong() }
        return JsonPrimitive(refreshTime ?: Ad.NO_REFRESH_TIME)
    }
}
