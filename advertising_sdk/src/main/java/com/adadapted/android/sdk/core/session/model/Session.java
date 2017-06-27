package com.adadapted.android.sdk.core.session.model;

import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.zone.model.Zone;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Session {
    private final DeviceInfo deviceInfo;
    private final String sessionId;
    private final boolean activeCampaigns;
    private final Date expiresAt;
    private final long pollingInterval;
    private final Map<String, Zone> zones;

    public Session(final DeviceInfo deviceInfo,
                   final String sessionId,
                   final boolean activeCampaigns,
                   final Date expiresAt,
                   final long pollingInterval,
                   final Map<String, Zone> zones) {
        this.deviceInfo = deviceInfo == null ? new DeviceInfo() : deviceInfo;
        this.sessionId = sessionId == null ? "" : sessionId;
        this.activeCampaigns = activeCampaigns;
        this.expiresAt = expiresAt == null ? new Date() : expiresAt;
        this.pollingInterval = pollingInterval;
        this.zones = zones == null ? new HashMap<String, Zone>() : zones;
    }

    public Session() {
        deviceInfo = new DeviceInfo();
        sessionId = "";
        activeCampaigns = false;
        expiresAt = new Date();
        pollingInterval = 300000;
        zones = new HashMap<>();
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public String getSessionId() {
        return sessionId;
    }

    public boolean hasActiveCampaigns() {
        return activeCampaigns;
    }

    public boolean hasExpired() {
        return expiresAt.getTime() <= (new Date().getTime());
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public long getPollingInterval() {
        return pollingInterval;
    }


    public Map<String, Zone> getZones() {
        return zones;
    }

    public Zone getZone(final String zoneId) {
        if(zones.containsKey(zoneId)) {
            return zones.get(zoneId);
        }

        return Zone.createEmptyZone(zoneId);
    }

    public Session updateZones(final Map<String, Zone> zones) {
        return new Session(
                getDeviceInfo(),
                getSessionId(),
                zones != null && zones.size() > 0,
                getExpiresAt(),
                getPollingInterval(),
                zones == null ? new HashMap<String, Zone>() : new HashMap<>(zones)
        );
    }

    @Override
    public String toString() {
        return "Session{}";
    }

    public static class Builder {
        private DeviceInfo deviceInfo;
        private String sessionId;
        private boolean activeCampaigns;
        private Date expiresAt;
        private long pollingInterval;
        private Map<String, Zone> zones;

        public Builder() {
            deviceInfo = new DeviceInfo();
            sessionId = "";
            activeCampaigns = false;
            expiresAt = new Date();
            pollingInterval = 300000;
            zones = new HashMap<>();
        }

        public DeviceInfo getDeviceInfo() {
            return deviceInfo;
        }

        public void setDeviceInfo(final DeviceInfo deviceInfo) {
            this.deviceInfo = deviceInfo;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(final String sessionId) {
            this.sessionId = sessionId;
        }

        public boolean hasActiveCampaigns() {
            return activeCampaigns;
        }

        public void setActiveCampaigns(final boolean activeCampaigns) {
            this.activeCampaigns = activeCampaigns;
        }

        public Date getExpiresAt() {
            return expiresAt;
        }

        public void setExpiresAt(final long expiresAt) {
            final long ea = expiresAt < 1000000000000L ? expiresAt * 1000 : expiresAt;
            this.expiresAt = new Date();
            this.expiresAt.setTime(ea);
        }

        public long getPollingInterval() {
            return pollingInterval;
        }

        public void setPollingInterval(final long pollingInterval) {
            this.pollingInterval = pollingInterval;
        }

        public Map<String, Zone> getZones() {
            return zones;
        }

        public void setZones(final Map<String, Zone> zones) {
            this.zones = zones;
        }

        public Session build() {
            return new Session(
                    deviceInfo,
                    sessionId,
                    activeCampaigns,
                    expiresAt,
                    pollingInterval,
                    zones);
        }
    }
}
