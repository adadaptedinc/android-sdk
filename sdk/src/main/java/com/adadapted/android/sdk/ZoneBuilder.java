package com.adadapted.android.sdk;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by chrisweeden on 3/27/15.
 */
class ZoneBuilder {
    private static final String TAG = ZoneBuilder.class.getName();

    private final AdBuilder adBuilder;

    ZoneBuilder() {
        this.adBuilder = new AdBuilder();
    }

    Map<String, Zone> buildZones(JSONObject jsonZones) {
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

    Zone buildZone(String zoneId, JSONObject jsonZone) {
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
            zone.getAds().addAll(adBuilder.buildAds(jsonAds));
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem converting to JSON.", ex);
        }

        return zone;
    }

    @Override
    public String toString() {
        return "ZoneBuilder{" +
                "adBuilder=" + adBuilder +
                '}';
    }
}
