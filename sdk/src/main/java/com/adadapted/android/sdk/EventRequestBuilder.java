package com.adadapted.android.sdk;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by chrisweeden on 3/23/15.
 */
class EventRequestBuilder {
    private static final String TAG = EventRequestBuilder.class.getName();

    JSONObject build(DeviceInfo deviceInfo, String sessionId, Ad ad, EventType eventType, String eventName) {
        JSONObject json = new JSONObject();

        try {
            json.put("app_id", deviceInfo.getAppId());
            json.put("session_id", sessionId);
            json.put("udid", deviceInfo.getUdid());
            json.put("ad_id", ad.getAdId());
            json.put("impression_id", ad.getImpressionId());
            json.put("event_type", eventType.toString());
            json.put("event_name", eventName);
            json.put("datetime", new Date().getTime());
            json.put("sdk_version", deviceInfo.getSdkVersion());
        }
        catch(JSONException ex) {
            Log.d(TAG, "Problem converting to JSON.", ex);
        }

        return json;
    }
}
