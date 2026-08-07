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
    @SerialName("created_at")
    val createdAt: Long = nowInSeconds()
) {
    companion object {
        fun forAd(ad: Ad, eventType: String) =
            AdEvent(ad.id, ad.zoneId, ad.impressionId, eventType)

        //Zone lifecycle events belong to the zone itself, not to any ad served into it
        fun forZone(zoneId: String, eventType: String) =
            AdEvent(adId = "", zoneId = zoneId, impressionId = "", eventType = eventType)
    }
}
