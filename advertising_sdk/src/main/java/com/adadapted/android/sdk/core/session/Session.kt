package com.adadapted.android.sdk.core.session

import com.adadapted.android.sdk.config.Config
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.zone.Zone
import com.google.gson.annotations.SerializedName
import java.util.Date
import kotlin.collections.HashMap

class Session(
    @SerializedName("session_id")
    val id: String = "",

    @SerializedName("will_serve_ads")
    private val willServeAds: Boolean = false,

    @SerializedName("active_campaigns")
    val hasAds: Boolean = false,

    @SerializedName("polling_interval_ms")
    val refreshTime: Long = Config.DEFAULT_AD_POLLING,

    @SerializedName("session_expires_at")
    val expiration: Long = 0,

    private var zones: Map<String, Zone> = HashMap()
) {

    constructor(session: Session, zones: Map<String, Zone>?) : this(
        session.id,
        session.willServeAds,
        session.hasActiveCampaigns(),
        session.refreshTime,
        session.expiration,
        zones ?: HashMap<String, Zone>()
    )

    private var deviceInfo: DeviceInfo = DeviceInfo()

    fun hasActiveCampaigns(): Boolean {
        return hasAds
    }

    fun hasExpired(): Boolean {
        return convertExpirationToDate(expiration).before(Date())
    }

    fun expiresAt(): Date {
        return convertExpirationToDate(expiration)
    }

    fun getZone(zoneId: String): Zone {
        if (zones.containsKey(zoneId)) {
            return zones[zoneId] ?: Zone()
        }
        return Zone()
    }

    fun setZones(zones: Map<String, Zone>) {
        this.zones = zones
    }

    fun setDeviceInfo(deviceInfo: DeviceInfo) {
        this.deviceInfo = deviceInfo
    }

    fun getDeviceInfo(): DeviceInfo {
        return this.deviceInfo
    }

    fun willNotServeAds(): Boolean {
        return !willServeAds || refreshTime == 0L
    }

    companion object {
        fun convertExpirationToDate(expireTime: Long): Date {
            return Date(expireTime * 1000)
        }
    }
}