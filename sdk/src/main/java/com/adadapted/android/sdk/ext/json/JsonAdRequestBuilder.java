package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdRequestBuilder;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by chrisweeden on 4/16/15.
 */
public class JsonAdRequestBuilder implements AdRequestBuilder {
    private static final String TAG = JsonAdRequestBuilder.class.getName();

    public JSONObject buildAdRequest(DeviceInfo deviceInfo, Session session) {
        JSONObject json = new JSONObject();

        try {
            json.put(JsonFields.APPID, deviceInfo.getAppId());
            json.put(JsonFields.UDID, deviceInfo.getUdid());
            json.put(JsonFields.SESSIONID, session.getSessionId());
            json.put(JsonFields.DATETIME, new Date().getTime());
            json.put(JsonFields.SDKVERSION, deviceInfo.getSdkVersion());

            String[] zones = deviceInfo.getZones();

            JSONArray zonesArray = new JSONArray();
            for(String zone : zones) {
                zonesArray.put(zone);
            }

            json.put(JsonFields.ZONES, zonesArray);
        }
        catch(JSONException ex) {
            Log.d(TAG, "Problem parsing JSON", ex);
        }

        return json;
    }
}
