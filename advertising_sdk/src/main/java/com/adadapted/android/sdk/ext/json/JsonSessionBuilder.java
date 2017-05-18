package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.session.SessionBuilder;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.core.zone.ZoneBuilder;
import com.adadapted.android.sdk.core.zone.model.Zone;
import com.adadapted.android.sdk.ext.management.AppEventTrackingManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 6/5/15.
 */
public class JsonSessionBuilder implements SessionBuilder {
    private static final String LOGTAG = JsonSessionBuilder.class.getName();

    private final DeviceInfo deviceInfo;
    private final ZoneBuilder zoneBuilder;

    public JsonSessionBuilder(final DeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
        this.zoneBuilder = new JsonZoneBuilder(deviceInfo.getScale());
    }

    public Session buildSession(final JSONObject response) {
        final Session.Builder builder = new Session.Builder();
        builder.setDeviceInfo(deviceInfo);

        try {
            builder.setSessionId(response.getString(JsonFields.SESSIONID));
            builder.setActiveCampaigns(response.getBoolean(JsonFields.ACTIVECAMPAIGNS));
            builder.setExpiresAt(response.getLong(JsonFields.SESSIONEXPIRESAT));
            builder.setPollingInterval(response.getLong(JsonFields.POLLINGINTERVALMS));

            if(builder.hasActiveCampaigns()) {
                if(response.has(JsonFields.ZONES) && (response.get(JsonFields.ZONES).getClass() == JSONObject.class)) {
                    final JSONObject jsonZones = response.getJSONObject(JsonFields.ZONES);
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
            AppEventTrackingManager.registerEvent(
                    "SESSION_PAYLOAD_PARSE_FAILED",
                    "Failed to parse Session payload for processing.",
                    params);
        }

        return builder.build();
    }
}
