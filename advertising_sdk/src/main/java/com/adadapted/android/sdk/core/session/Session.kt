package com.adadapted.android.sdk.core.session

import com.adadapted.android.sdk.constants.Config
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.view.Zone
import kotlinx.datetime.Clock
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Session(
    @SerialName("session_id")
    val id: String = "",
    @SerialName("will_serve_ads")
    private val willServeAds: Boolean = false,
    @SerialName("active_campaigns")
    val hasAds: Boolean = false,
    @SerialName("polling_interval_ms")
    val refreshTime: Long = Config.DEFAULT_AD_POLLING,
    @SerialName("session_expires_at")
    val expiration: Long = 0,
    private var zones: Map<String, Zone> = HashMap()
) {

    var deviceInfo: DeviceInfo = DeviceInfo()

    fun hasActiveCampaigns(): Boolean {
        return hasAds
    }

    fun hasExpired(): Boolean {
        return Clock.System.now().epochSeconds > expiration
    }

    fun getZone(zoneId: String): Zone {
        if (zones.containsKey(zoneId)) {
            return zones[zoneId] ?: Zone()
        }
        return Zone()
    }

    fun getAllZones(): Map<String, Zone> {
        return zones
    }

    fun getZonesWithAds(): List<String> {
        val activeZones = mutableListOf<String>()
        zones.forEach { zone -> if (zone.value.ads.any()) activeZones.add(zone.value.id) }
        return activeZones
    }

    fun updateZones(newZones: Map<String, Zone>) {
        zones = newZones
    }

    fun willNotServeAds(): Boolean {
        return !willServeAds || refreshTime == 0L
    }
}