package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.session.SessionBuilder;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.core.zone.ZoneBuilder;
import com.adadapted.android.sdk.core.zone.model.Zone;
import com.adadapted.android.sdk.ext.factory.AnomalyTrackerFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by chrisweeden on 6/5/15.
 */
public class JsonSessionBuilder implements SessionBuilder {
    private static final String LOGTAG = JsonSessionBuilder.class.getName();

    private final ZoneBuilder mZoneBuilder;

    public JsonSessionBuilder(final float deviceScale) {
        mZoneBuilder = new JsonZoneBuilder(deviceScale);
    }

    public Session buildSession(final DeviceInfo deviceInfo,
                                final JSONObject response) {
        final Session session = new Session();
        session.setDeviceInfo(deviceInfo);

        try {
            if(response.has(JsonFields.SESSIONID)) {
                session.setSessionId(response.getString(JsonFields.SESSIONID));
            }

            if(response.has(JsonFields.ACTIVECAMPAIGNS)) {
                session.setActiveCampaigns(response.getBoolean(JsonFields.ACTIVECAMPAIGNS));
            }

            if(response.has(JsonFields.SESSIONEXPIRESAT)) {
                session.setExpiresAt(response.getLong(JsonFields.SESSIONEXPIRESAT));
            }

            if(response.has(JsonFields.POLLINGINTERVALMS)) {
                session.setPollingInterval(response.getLong(JsonFields.POLLINGINTERVALMS));
            }

            if(session.hasActiveCampaigns()) {
                if(response.has(JsonFields.ZONES)) {
                    final JSONObject jsonZones = response.getJSONObject(JsonFields.ZONES);
                    final Map<String, Zone> zones = mZoneBuilder.buildZones(jsonZones);

                    session.updateZones(zones);
                }
            }
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem converting to JSON.", ex);
            AnomalyTrackerFactory.registerAnomaly("",
                    response.toString(),
                    "SESSION_PAYLOAD_PARSE_FAILED",
                    "Failed to parse Session payload for processing.");
        }

        return session;
    }
}
