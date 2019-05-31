package com.adadapted.android.sdk.core.session;

import com.adadapted.android.sdk.config.Config;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.zone.Zone;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Session {
    private final DeviceInfo deviceInfo;
    private final String id;
    private final boolean hasAds;
    private final long refreshTime;
    private final Date expiresAt;
    private final Map<String, Zone> zones;

    public Session(final DeviceInfo deviceInfo,
                   final String id,
                   final boolean hasAds,
                   final long refreshTime,
                   final Date expiresAt,
                   final Map<String, Zone> zones) {
        this.deviceInfo = deviceInfo;
        this.id = id == null ? "" : id;
        this.hasAds = hasAds;
        this.refreshTime = refreshTime;
        this.expiresAt = expiresAt;
        this.zones = (zones != null) ? zones : new HashMap<String, Zone>();
    }

    public Session(final Session session,
                   final Map<String, Zone> zones) {
        this(
            session.getDeviceInfo(),
            session.getId(),
            session.hasActiveCampaigns(),
            session.getRefreshTime(),
            session.getExpiresAt(),
            (zones != null) ? zones : new HashMap<String, Zone>()
        );
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public String getId() {
        return id;
    }

    public boolean hasActiveCampaigns() {
        return hasAds;
    }

    public long getRefreshTime() {
        return refreshTime;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public boolean hasExpired() {
        return getExpiresAt().before(new Date());
    }

    public Map<String, Zone> getZones() {
        return zones;
    }

    public Zone getZone(final String zoneId) {
        if(zones.containsKey(zoneId)) {
            return zones.get(zoneId);
        }

        return Zone.emptyZone();
    }

    public static Session emptySession(final DeviceInfo deviceInfo) {
        return new Session(deviceInfo, "", false, Config.DEFAULT_AD_POLLING, new Date(), new HashMap<String, Zone>());
    }

    public static class Builder {
        private DeviceInfo deviceInfo;
        private String sessionId;
        private boolean hasAds;
        private long refreshTime;
        private Date expiresAt;
        private Map<String, Zone> zones;

        public Builder() {
            sessionId = "";
            hasAds = false;
            refreshTime = 0L;
            expiresAt = new Date();
            zones = new HashMap<>();
        }

        public DeviceInfo getDeviceInfo() {
            return deviceInfo;
        }

        public void setDeviceInfo(DeviceInfo deviceInfo) {
            this.deviceInfo = deviceInfo;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public boolean hasActiveCampaigns() {
            return hasAds;
        }

        public void setActiveCampaigns(boolean hasAds) {
            this.hasAds = hasAds;
        }

        public long getPollingInterval() {
            return refreshTime;
        }

        public void setPollingInterval(long refreshTime) {
            this.refreshTime = refreshTime;
        }

        public Date getExpiresAt() {
            return expiresAt;
        }

        public void setExpiresAt(long expiresAt) {
            this.expiresAt = new Date(expiresAt * 1000);
        }

        public Map<String, Zone> getZones() {
            return zones;
        }

        public void setZones(Map<String, Zone> zones) {
            this.zones = zones;
        }

        public Session build() {
            return new Session(
                getDeviceInfo(),
                getSessionId(),
                hasActiveCampaigns(),
                getPollingInterval(),
                getExpiresAt(),
                getZones()
            );
        }
    }
}
