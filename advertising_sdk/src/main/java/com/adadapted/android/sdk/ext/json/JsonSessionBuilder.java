package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.session.SessionBuilder;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.core.zone.ZoneBuilder;
import com.adadapted.android.sdk.core.zone.model.Zone;
import com.adadapted.android.sdk.ext.management.AdAnomalyTrackingManager;

import org.json.JSONException;
import org.json.JSONObject;

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
            if(response.has(JsonFields.SESSIONID)) {
                builder.setSessionId(response.getString(JsonFields.SESSIONID));
            }

            if(response.has(JsonFields.ACTIVECAMPAIGNS)) {
                builder.setActiveCampaigns(response.getBoolean(JsonFields.ACTIVECAMPAIGNS));
            }

            if(response.has(JsonFields.SESSIONEXPIRESAT)) {
                builder.setExpiresAt(response.getLong(JsonFields.SESSIONEXPIRESAT));
            }

            if(response.has(JsonFields.POLLINGINTERVALMS)) {
                builder.setPollingInterval(response.getLong(JsonFields.POLLINGINTERVALMS));
            }

            if(builder.hasActiveCampaigns()) {
                if(response.has(JsonFields.ZONES)) {
                    final JSONObject jsonZones = response.getJSONObject(JsonFields.ZONES);
                    final Map<String, Zone> zones = zoneBuilder.buildZones(jsonZones);

                    builder.setZones(zones);
                }
            }
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem converting to JSON.", ex);
            AdAnomalyTrackingManager.registerAnomaly("",
                    response.toString(),
                    "SESSION_PAYLOAD_PARSE_FAILED",
                    "Failed to parse Session payload for processing.");
        }

        return builder.build();
    }
}
