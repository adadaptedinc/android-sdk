package com.adadapted.android.sdk;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by chrisweeden on 4/1/15.
 */
class AdRequestBuilder {
    private static final String TAG = AdRequestBuilder.class.getName();

    JSONObject buildAdRequestJson(DeviceInfo deviceInfo, Session session) {
        JSONObject json = new JSONObject();

        try {
            json.put("app_id", deviceInfo.getAppId());
            json.put("udid", deviceInfo.getUdid());
            json.put("session_id", session.getSessionId());
            json.put("zones", deviceInfo.getZones());
            json.put("datetime", new Date().getTime());
        }
        catch(JSONException ex) {
           Log.d(TAG, "Problem parsing JSON", ex);
        }

        return json;
    }
}
