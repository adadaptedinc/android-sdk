package com.adadapted.android.sdk.core.session.model;

import com.adadapted.android.sdk.core.zone.model.Zone;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class Session {
    private static final String TAG = Session.class.getName();

    private String sessionId = "";
    private boolean activeCampaigns = false;
    private Date expiresAt;
    private long pollingInterval = 300000;
    private final Map<String, Zone> zones;

    public Session() {
        this.zones = new HashMap<>();
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
        this.zones.clear();
        this.zones.putAll(zones);
    }

    @Override
    public String toString() {
        return "Session{" +
                "sessionId='" + sessionId + '\'' +
                ", activeCampaigns=" + activeCampaigns +
                ", expiresAt=" + expiresAt +
                ", pollingInterval=" + pollingInterval +
                ", zones=" + zones +
                '}';
    }
}
