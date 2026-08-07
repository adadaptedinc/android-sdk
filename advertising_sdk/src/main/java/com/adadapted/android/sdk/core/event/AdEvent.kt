package com.adadapted.android.sdk.core.event

import com.adadapted.android.sdk.core.ad.Ad
import com.adadapted.android.sdk.core.concurrency.nowInSeconds
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdEvent(
    @SerialName("ad_id")
    val adId: String,
    @SerialName("zone_id")
    val zoneId: String,
    @SerialName("impression_id")
    val impressionId: String,
    @SerialName("event_type")
    val eventType: String,
    @SerialName("event_name") //Optional server-side field. Left out of the payload entirely when null
    val eventName: String? = null,
    @SerialName("created_at")
    val createdAt: Long = nowInSeconds()
) {
    companion object {
        fun forAd(ad: Ad, eventType: String, eventName: String? = null) =
            AdEvent(ad.id, ad.zoneId, ad.impressionId, eventType, eventName)

        fun forZone(zoneId: String, eventType: String, eventName: String? = null) =
            AdEvent(
                adId = "",
                zoneId = zoneId,
                impressionId = "",
                eventType = eventType,
                eventName = eventName
            )
    }
}
