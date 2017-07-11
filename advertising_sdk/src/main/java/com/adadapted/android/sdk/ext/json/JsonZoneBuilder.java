package com.adadapted.android.sdk.ext.json;

import android.util.Log;

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

    private final JsonAdBuilder mAdBuilder;
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

            final Map<String, String> errorParams = new HashMap<>();
            errorParams.put("bad_json", jsonZones.toString());
            errorParams.put("exception", ex.getMessage());

            AppEventClient.trackError(
                "SESSION_ZONE_PAYLOAD_PARSE_FAILED",
                "Failed to parse Session Zone payload for processing.",
                errorParams
            );
        }

        return zones;
    }

    private Zone buildZone(final String zoneId,
                           final JSONObject jsonZone) throws JSONException{
        final Zone.Builder builder = new Zone.Builder();
        builder.setZoneId(zoneId);

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

            builder.getDimensions().put(Dimension.ORIEN.PORT, portDimension);
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

            builder.getDimensions().put(Dimension.ORIEN.LAND, landDimension);
        }

        final JSONArray jsonAds = jsonZone.getJSONArray(JsonFields.ADS);
        builder.setAds(mAdBuilder.buildAds(jsonAds));

        return builder.build();
    }

    private int calculateDimensionValue(final String value) {
        return mDimensionConverter.convertDpToPx(Integer.parseInt(value));
    }
}
