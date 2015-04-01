package com.adadapted.android.sdk;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by chrisweeden on 3/23/15.
 */
class SessionBuilder {
    private static final String TAG = SessionBuilder.class.getName();

    private final ZoneBuilder zoneBuilder;

    SessionBuilder() {
        this.zoneBuilder = new ZoneBuilder();
    }

    Session buildSession(JSONObject response) {
        Session session = new Session();

        try {
            session.setSessionId(response.getString("session_id"));
            session.setActiveCampaigns(response.getBoolean("active_campaigns"));
            session.setExpiresAt(response.getLong("session_expires_at"));
            session.setPollingInterval(response.getLong("polling_interval_ms"));

            JSONObject jsonZones = response.getJSONObject("zones");
            Map<String, Zone> zones = zoneBuilder.buildZones(jsonZones);
            session.getZones().putAll(zones);
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem converting to JSON.", ex);
        }

        return session;
    }

    @Override
    public String toString() {
        return "SessionBuilder{" +
                "zoneBuilder=" + zoneBuilder +
                '}';
    }
}
