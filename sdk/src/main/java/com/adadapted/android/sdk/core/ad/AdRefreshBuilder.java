package com.adadapted.android.sdk.core.ad;

import android.util.Log;

import com.adadapted.android.sdk.core.zone.Zone;
import com.adadapted.android.sdk.core.zone.ZoneBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chrisweeden on 4/16/15.
 */
public class AdRefreshBuilder {
    private static final String TAG = AdRefreshBuilder.class.getName();

    private final ZoneBuilder zoneBuilder;

    public AdRefreshBuilder() {
        zoneBuilder = new ZoneBuilder();
    }

    public Map<String, Zone> buildRefreshedAds(JSONObject adJson) {
        try {
            return zoneBuilder.buildZones(adJson.getJSONObject("zones"));
        }
        catch (JSONException ex) {
            Log.w(TAG, "Problem converting to JSON.", ex);
        }

        return new HashMap<>();
    }
}
