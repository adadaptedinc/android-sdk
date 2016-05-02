package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdBuilder;
import com.adadapted.android.sdk.core.common.Dimension;
import com.adadapted.android.sdk.core.common.DimensionConverter;
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
    private static final String LOGTAG = JsonZoneBuilder.class.getName();

    private final AdBuilder mAdBuilder;
    private final DimensionConverter mDimensionConverter;

    public JsonZoneBuilder(final float deviceScale) {
        mDimensionConverter = new DimensionConverter(deviceScale);
        mAdBuilder = new JsonAdBuilder();
    }

    public Map<String, Zone> buildZones(final JSONObject jsonZones) {
        final Map<String, Zone> zones = new HashMap<>();

        try {
            for(final Iterator<String> z = jsonZones.keys(); z.hasNext();) {
                final String zoneId = z.next();
                final JSONObject jsonZone = jsonZones.getJSONObject(zoneId);
                final Zone zone = buildZone(zoneId, jsonZone);

                zones.put(zoneId, zone);
            }
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem converting to JSON.", ex);
        }

        return zones;
    }

    public Zone buildZone(final String zoneId,
                          final JSONObject jsonZone) {
        final Zone zone = new Zone(zoneId);

        try {
            if(jsonZone.has(JsonFields.PORTZONEHEIGHT) && jsonZone.has(JsonFields.PORTZONEWIDTH)) {
                final Dimension portDimension = new Dimension();

                if(jsonZone.has(JsonFields.PORTZONEHEIGHT)) {
                    portDimension.setHeight(calculateDimensionValue(jsonZone.getString(JsonFields.PORTZONEHEIGHT)));
                }
                else {
                    portDimension.setHeight(Dimension.WRAP_CONTENT);
                }

                if(jsonZone.has(JsonFields.PORTZONEWIDTH)) {
                    portDimension.setWidth(calculateDimensionValue(jsonZone.getString(JsonFields.PORTZONEWIDTH)));
                }
                else {
                    portDimension.setWidth(Dimension.MATCH_PARENT);
                }

                zone.getDimensions().put(Dimension.ORIEN.PORT, portDimension);
            }

            if(jsonZone.has(JsonFields.LANDZONEHEIGHT) && jsonZone.has(JsonFields.LANDZONEWIDTH)) {
                final Dimension landDimension = new Dimension();

                if(jsonZone.has(JsonFields.LANDZONEHEIGHT)) {
                    landDimension.setHeight(calculateDimensionValue(jsonZone.getString(JsonFields.LANDZONEHEIGHT)));
                }
                else {
                    landDimension.setHeight(Dimension.WRAP_CONTENT);
                }

                if(jsonZone.has(JsonFields.LANDZONEWIDTH)) {
                    landDimension.setWidth(calculateDimensionValue(jsonZone.getString(JsonFields.LANDZONEWIDTH)));
                }
                else {
                    landDimension.setWidth(Dimension.MATCH_PARENT);
                }

                zone.getDimensions().put(Dimension.ORIEN.LAND, landDimension);
            }

            final JSONArray jsonAds = jsonZone.getJSONArray(JsonFields.ADS);
            zone.setAds(mAdBuilder.buildAds(jsonAds));
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem converting to JSON.", ex);
        }

        return zone;
    }

    public int calculateDimensionValue(final String value) {
        return mDimensionConverter.convertDpToPx(Integer.parseInt(value));
    }
}
