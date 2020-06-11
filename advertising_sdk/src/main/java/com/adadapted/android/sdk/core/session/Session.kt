package com.adadapted.android.sdk.core.session

import com.adadapted.android.sdk.config.Config
import com.adadapted.android.sdk.core.device.DeviceInfo
import com.adadapted.android.sdk.core.zone.Zone
import java.util.Date
import kotlin.collections.HashMap


class Session(val deviceInfo: DeviceInfo,
              val id: String = "",
              private val willServeAds: Boolean = false,
              val hasAds: Boolean = false,
              val refreshTime: Long = Config.DEFAULT_AD_POLLING,
              val expiresAt: Date = Date(),
              private var zones: Map<String, Zone> = HashMap()) {

    constructor(session: Session, zones: Map<String, Zone>?) : this(
            session.deviceInfo,
            session.id,
            session.willServeAds,
            session.hasActiveCampaigns(),
            session.refreshTime,
            session.expiresAt,

            zones ?: HashMap<String, Zone>()
    )

    fun hasActiveCampaigns(): Boolean {
        return hasAds
    }

    fun hasExpired(): Boolean {
        return expiresAt.before(Date())
    }

    fun getZone(zoneId: String): Zone {
        if (zones.containsKey(zoneId)) {
            return zones[zoneId] ?: Zone.emptyZone()
            }

        return Zone.emptyZone()
    }

    fun setZones(zones: Map<String, Zone>) {
        this.zones = zones
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