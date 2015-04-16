package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdRequestBuilder;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.session.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by chrisweeden on 4/16/15.
 */
public class JsonAdRequestBuilder implements AdRequestBuilder {
    public static final String TAG = JsonAdRequestBuilder.class.getName();

    public JSONObject buildAdRequest(DeviceInfo deviceInfo, Session session) {
        JSONObject json = new JSONObject();

        try {
            json.put("app_id", deviceInfo.getAppId());
            json.put("udid", deviceInfo.getUdid());
            json.put("session_id", session.getSessionId());
            json.put("datetime", new Date().getTime());
            json.put("sdk_version", deviceInfo.getSdkVersion());

            String[] zones = deviceInfo.getZones();

            JSONArray zonesArray = new JSONArray();
            for(String zone : zones) {
                zonesArray.put(zone);
            }

            json.put("zones", zonesArray);
        }
        catch(JSONException ex) {
            Log.d(TAG, "Problem parsing JSON", ex);
        }

        return json;
    }
}
