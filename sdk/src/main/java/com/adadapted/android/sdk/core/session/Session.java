package com.adadapted.android.sdk.core.session;

import com.adadapted.android.sdk.core.zone.Zone;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class Session {
    private static final String TAG = Session.class.getName();

    private String sessionId;
    private boolean activeCampaigns;
    private Date expiresAt;
    private long pollingInterval;
    private final Map<String, Zone> zones;

    Session() {
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
        return zones.get(zoneId);
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
