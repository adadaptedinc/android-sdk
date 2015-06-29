package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.session.SessionBuilder;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.core.zone.ZoneBuilder;
import com.adadapted.android.sdk.core.zone.model.Zone;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by chrisweeden on 6/5/15.
 */
public class JsonSessionBuilder implements SessionBuilder {
    private static final String TAG = JsonSessionBuilder.class.getName();

    private final ZoneBuilder zoneBuilder;

    public JsonSessionBuilder() {
        this.zoneBuilder = new ZoneBuilder();
    }

    public Session buildSession(JSONObject response) {
        Session session = new Session();

        try {
            session.setSessionId(response.getString("session_id"));
            session.setActiveCampaigns(response.getBoolean("active_campaigns"));
            session.setExpiresAt(response.getLong("session_expires_at"));
            session.setPollingInterval(response.getLong("polling_interval_ms"));

            if(session.hasActiveCampaigns()) {
                JSONObject jsonZones = response.getJSONObject("zones");
                Map<String, Zone> zones = zoneBuilder.buildZones(jsonZones);

                session.updateZones(zones);
            }
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem converting to JSON.", ex);
        }

        return session;
    }
}
