package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.session.Session;
import com.adadapted.android.sdk.core.zone.Zone;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonSessionBuilder {
    private static final String LOGTAG = JsonSessionBuilder.class.getName();

    private static final String SESSION_ID = "session_id";
    private static final String ACTIVE_CAMPAIGNS = "active_campaigns";
    private static final String SESSION_EXPIRES_AT = "session_expires_at";
    private static final String POLLING_INTERVAL_MS = "polling_interval_ms";
    private static final String ZONES = "zones";

    private final DeviceInfo deviceInfo;
    private final JsonZoneBuilder zoneBuilder;

    public JsonSessionBuilder(final DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
        this.zoneBuilder = new JsonZoneBuilder(deviceInfo.getScale());
    }

    public Session buildSession(final JSONObject response) {
        final Session.Builder builder = new Session.Builder();
        builder.setDeviceInfo(deviceInfo);

        try {
            builder.setSessionId(response.getString(SESSION_ID));
            builder.setActiveCampaigns(response.getBoolean(ACTIVE_CAMPAIGNS));
            builder.setExpiresAt(response.getLong(SESSION_EXPIRES_AT));
            builder.setPollingInterval(response.getLong(POLLING_INTERVAL_MS));

            if(builder.hasActiveCampaigns()) {
                if(response.has(ZONES) && (response.get(ZONES).getClass() == JSONObject.class)) {
                    final JSONObject jsonZones = response.getJSONObject(ZONES);
                    final Map<String, Zone> zones = zoneBuilder.buildZones(jsonZones);
                    builder.setZones(zones);
                }
                else {
                    Log.i(LOGTAG, "No ads returned. Not parsing JSONArray.");
                }
            }
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem converting to JSON.", ex);

            final Map<String, String> params = new HashMap<>();
            params.put("exception", ex.getMessage());
            params.put("bad_json", response.toString());
            AppEventClient.trackError(
                "SESSION_PAYLOAD_PARSE_FAILED",
                "Failed to parse Session payload for processing.",
                params
            );
        }

        return builder.build();
    }
}
