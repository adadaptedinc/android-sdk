package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.config.EventStrings;
import com.adadapted.android.sdk.core.common.Dimension;
import com.adadapted.android.sdk.core.common.DimensionConverter;
import com.adadapted.android.sdk.core.event.AppEventClient;
import com.adadapted.android.sdk.core.zone.Zone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonZoneBuilder {
    private static final String LOGTAG = JsonZoneBuilder.class.getName();

    private static final String ADS = "ads";
    private static final String PORT_ZONE_HEIGHT = "port_height";
    private static final String PORT_ZONE_WIDTH = "port_width";
    private static final String LAND_ZONE_HEIGHT = "land_height";
    private static final String LAND_ZONE_WIDTH = "land_width";

    private final JsonAdBuilder mAdBuilder;
    private final DimensionConverter mDimensionConverter;

    public JsonZoneBuilder(final float deviceScale) {
        mDimensionConverter = new DimensionConverter(deviceScale);
        mAdBuilder = new JsonAdBuilder();
    }

    public Map<String, Zone> buildZones(final JSONObject jsonZones) {
        final Map<String, Zone> zones = new HashMap<>();

        for(final Iterator<String> z = jsonZones.keys(); z.hasNext();) {
            try {
                final String zoneId = z.next();
                final JSONObject jsonZone = jsonZones.getJSONObject(zoneId);
                final Zone zone = buildZone(zoneId, jsonZone);

                zones.put(zoneId, zone);
            }
            catch(Exception ex) {
                Log.w(LOGTAG, "Problem converting to JSON.", ex);

                final Map<String, String> errorParams = new HashMap<>();
                errorParams.put("bad_json", jsonZones.toString());
                errorParams.put("exception", ex.getMessage());

                AppEventClient.Companion.getInstance().trackError(
                        EventStrings.SESSION_ZONE_PAYLOAD_PARSE_FAILED,
                    "Failed to parse Session Zone payload for processing.",
                    errorParams
                );
            }
        }

        return zones;
    }

    private Zone buildZone(final String zoneId,
                           final JSONObject jsonZone) throws JSONException, NumberFormatException{
        final Zone.Builder builder = new Zone.Builder();
        builder.setZoneId(zoneId);

        if(jsonZone.has(PORT_ZONE_HEIGHT) && jsonZone.has(PORT_ZONE_WIDTH)) {
            final Dimension portDimension = new Dimension();

            if(jsonZone.has(PORT_ZONE_HEIGHT)) {
                portDimension.setHeight(calculateDimensionValue(jsonZone.getString(PORT_ZONE_HEIGHT)));
            }
            else {
                portDimension.setHeight(Dimension.WRAP_CONTENT);
            }

            if(jsonZone.has(PORT_ZONE_WIDTH)) {
                portDimension.setWidth(calculateDimensionValue(jsonZone.getString(PORT_ZONE_WIDTH)));
            }
            else {
                portDimension.setWidth(Dimension.MATCH_PARENT);
            }

            builder.setDimension(Dimension.Orientation.PORT, portDimension);
        }

        if(jsonZone.has(LAND_ZONE_HEIGHT) && jsonZone.has(LAND_ZONE_WIDTH)) {
            final Dimension landDimension = new Dimension();

            if(jsonZone.has(LAND_ZONE_HEIGHT)) {
                landDimension.setHeight(calculateDimensionValue(jsonZone.getString(LAND_ZONE_HEIGHT)));
            }
            else {
                landDimension.setHeight(Dimension.WRAP_CONTENT);
            }

            if(jsonZone.has(LAND_ZONE_WIDTH)) {
                landDimension.setWidth(calculateDimensionValue(jsonZone.getString(LAND_ZONE_WIDTH)));
            }
            else {
                landDimension.setWidth(Dimension.MATCH_PARENT);
            }

            builder.setDimension(Dimension.Orientation.LAND, landDimension);
        }

        final JSONArray jsonAds = jsonZone.getJSONArray(ADS);
        builder.setAds(mAdBuilder.buildAds(zoneId, jsonAds));

        return builder.build();
    }

    private int calculateDimensionValue(final String value) throws NumberFormatException {
        return mDimensionConverter.convertDpToPx(Integer.parseInt(value));
    }
}
