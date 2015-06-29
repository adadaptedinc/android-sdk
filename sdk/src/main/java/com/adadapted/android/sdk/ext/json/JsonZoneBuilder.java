package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdBuilder;
import com.adadapted.android.sdk.core.common.Dimension;
import com.adadapted.android.sdk.core.zone.ZoneBuilder;
import com.adadapted.android.sdk.core.zone.model.Zone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by chrisweeden on 6/29/15.
 */
public class JsonZoneBuilder implements ZoneBuilder {
    private static final String TAG = JsonZoneBuilder.class.getName();

    private final AdBuilder adBuilder;

    public JsonZoneBuilder() {
        this.adBuilder = new JsonAdBuilder();
    }

    public Map<String, Zone> buildZones(JSONObject jsonZones) {
        Map<String, Zone> zones = new HashMap<>();

        try {
            for(Iterator<String> z = jsonZones.keys(); z.hasNext();)
            {
                String zoneId = z.next();
                JSONObject jsonZone = jsonZones.getJSONObject(zoneId);
                Zone zone = buildZone(zoneId, jsonZone);

                zones.put(zoneId, zone);
            }
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem converting to JSON.", ex);
        }

        return zones;
    }

    public Zone buildZone(String zoneId, JSONObject jsonZone) {
        Zone zone = new Zone(zoneId);

        try {
            if(jsonZone.has("port_zone_height") && jsonZone.has("port_zone_width")) {
                Dimension portDimension = new Dimension();

                portDimension.setHeight(Integer.parseInt(jsonZone.getString("port_zone_height")));
                portDimension.setWidth(Integer.parseInt(jsonZone.getString("port_zone_width")));

                zone.getDimensions().put("port", portDimension);
            }

            if(jsonZone.has("land_zone_height") && jsonZone.has("land_zone_width")) {
                Dimension landDimension = new Dimension();

                landDimension.setHeight(Integer.parseInt(jsonZone.getString("land_zone_height")));
                landDimension.setWidth(Integer.parseInt(jsonZone.getString("land_zone_width")));

                zone.getDimensions().put("land", landDimension);
            }

            JSONArray jsonAds = jsonZone.getJSONArray("ads");
            zone.setAds(adBuilder.buildAds(jsonAds));
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem converting to JSON.", ex);
        }

        return zone;
    }
}
