package com.adadapted.android.sdk;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by chrisweeden on 3/23/15.
 */
class SessionRequestBuilder {
    private static final String TAG = SessionRequestBuilder.class.getName();

    JSONObject buildSessionRequestJson(DeviceInfo deviceInfo) {
        JSONObject json = new JSONObject();

        try {
            json.put("app_id", deviceInfo.getAppId());
            json.put("udid", deviceInfo.getUdid());
            json.put("bundle_id", deviceInfo.getBundleId());
            json.put("device", deviceInfo.getDevice());
            json.put("os", deviceInfo.getOs());
            json.put("osv", deviceInfo.getOsv());
            json.put("locale", deviceInfo.getLocale());
            json.put("timezone", deviceInfo.getTimezone());
            json.put("dh", deviceInfo.getDh());
            json.put("dw", deviceInfo.getDw());
            json.put("datetime", new Date().getTime());
            json.put("allow_retargeting", 1);
            json.put("sdk_version", deviceInfo.getSdkVersion());

            String[] zones = deviceInfo.getZones();

            JSONArray zonesArray = new JSONArray();
            for(String zone : zones) {
                zonesArray.put(zone);
            }

            json.put("zones", zonesArray);
        }
        catch(JSONException ex) {
            Log.d(TAG, "Problem converting to JSON.", ex);
        }

        return json;
    }
}
