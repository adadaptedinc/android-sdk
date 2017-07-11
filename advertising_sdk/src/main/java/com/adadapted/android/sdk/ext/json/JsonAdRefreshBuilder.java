package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.zone.Zone;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonAdRefreshBuilder {
    private static final String LOGTAG = JsonAdRefreshBuilder.class.getName();

    private final JsonZoneBuilder mZoneBuilder;

    public JsonAdRefreshBuilder(final JsonZoneBuilder zoneBuilder) {
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

            AppEventClient.trackError(
                "SESSION_AD_PAYLOAD_PARSE_FAILED",
                "Failed to parse Ad payload for processing.",
                errorParams
            );
        }

        return new HashMap<>();
    }
}
