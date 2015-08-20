package com.adadapted.android.sdk.core.session.model;

import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.zone.model.Zone;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class Session {
    private static final String TAG = Session.class.getName();

    private DeviceInfo deviceInfo;
    private String sessionId;
    private boolean activeCampaigns;
    private Date expiresAt;
    private long pollingInterval;
    private Map<String, Zone> zones;

    public Session() {
        sessionId = "";
        activeCampaigns = false;
        pollingInterval = 300000;
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
        return activeCampaigns;
    }

    public void setActiveCampaigns(boolean activeCampaigns) {
        this.activeCampaigns = activeCampaigns;
    }

    public boolean hasExpired() {
        return expiresAt != null && (expiresAt.getTime() <= (new Date().getTime()));
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setExpiresAt(Long expiresAtTime) {
        this.expiresAt = new Date();
        this.expiresAt.setTime(expiresAtTime * 1000);
    }

    public long getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(long pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    public Map<String, Zone> getZones() {
        return zones;
    }

    public Zone getZone(String zoneId) {
        Zone zone = zones.get(zoneId);

        if(zone == null) {
            zone = Zone.createEmptyZone(zoneId);
        }

        return zone;
    }

    public void updateZones(Map<String, Zone> zones) {
        this.zones = new HashMap<>(zones);
    }

    @Override
    public String toString() {
        return "Session{}";
    }
}
