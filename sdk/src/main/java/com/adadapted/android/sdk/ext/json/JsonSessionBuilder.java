package com.adadapted.android.sdk.ext.json;

import android.content.Context;
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

    public JsonSessionBuilder(Context context) {
        this.zoneBuilder = new JsonZoneBuilder(context);
    }

    public Session buildSession(JSONObject response) {
        Session session = new Session();

        try {
            if(response.has("session_id")) {
                session.setSessionId(response.getString("session_id"));
            }

            if(response.has("active_campaigns")) {
                session.setActiveCampaigns(response.getBoolean("active_campaigns"));
            }

            if(response.has("session_expires_at")) {
                session.setExpiresAt(response.getLong("session_expires_at"));
            }

            if(response.has("polling_interval_ms")) {
                session.setPollingInterval(response.getLong("polling_interval_ms"));
            }

            if(session.hasActiveCampaigns()) {
                if(response.has("zones")) {
                    JSONObject jsonZones = response.getJSONObject("zones");
                    Map<String, Zone> zones = zoneBuilder.buildZones(jsonZones);

                    session.updateZones(zones);
                }
            }
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem converting to JSON.", ex);
        }

        return session;
    }
}
