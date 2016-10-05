package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdRefreshBuilder;
import com.adadapted.android.sdk.core.zone.ZoneBuilder;
import com.adadapted.android.sdk.core.zone.model.Zone;
import com.adadapted.android.sdk.ext.management.AdAnomalyTrackingManager;

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
        try {
            return mZoneBuilder.buildZones(adJson.getJSONObject(JsonFields.ZONES));
        }
        catch (JSONException ex) {
            Log.w(LOGTAG, "Problem converting to JSON.");
            AdAnomalyTrackingManager.registerAnomaly("",
                    adJson.toString(),
                    "SESSION_AD_PAYLOAD_PARSE_FAILED",
                    "Failed to parse Ad payload for processing.");
        }

        return new HashMap<>();
    }
}
