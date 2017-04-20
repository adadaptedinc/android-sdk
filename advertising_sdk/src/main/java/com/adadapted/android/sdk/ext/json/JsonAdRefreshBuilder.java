package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdRefreshBuilder;
import com.adadapted.android.sdk.core.zone.ZoneBuilder;
import com.adadapted.android.sdk.core.zone.model.Zone;
import com.adadapted.android.sdk.ext.management.AppErrorTrackingManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 8/19/15.
 */
public class JsonAdRefreshBuilder implements AdRefreshBuilder {
    private static final String LOGTAG = JsonAdRefreshBuilder.class.getName();

    private final ZoneBuilder mZoneBuilder;

    public JsonAdRefreshBuilder(final ZoneBuilder zoneBuilder) {
        mZoneBuilder = zoneBuilder;
    }

    public Map<String, Zone> buildRefreshedAds(final JSONObject adJson) {
        Map<String, Zone> zones = new HashMap<>();
        try {
            if(adJson.has(JsonFields.ZONES) && (adJson.get(JsonFields.ZONES).getClass() == JSONObject.class)) {
                zones = mZoneBuilder.buildZones(adJson.getJSONObject(JsonFields.ZONES));
            } else {
                Log.i(LOGTAG, "No ads returned. Not parsing JSONArray.");
            }

            return zones;
        }
        catch (JSONException ex) {
            Log.w(LOGTAG, "Problem converting to JSON.");

            final Map<String, String> errorParams = new HashMap<>();
            errorParams.put("bad_json", adJson.toString());
            errorParams.put("exception", ex.getMessage());

            AppErrorTrackingManager.registerEvent(
                    "SESSION_AD_PAYLOAD_PARSE_FAILED",
                    "Failed to parse Ad payload for processing.",
                    errorParams);
        }

        return new HashMap<>();
    }
}
