package com.adadapted.android.sdk.core.event;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.Ad;
import com.adadapted.android.sdk.core.device.DeviceInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class EventRequestBuilder {
    private static final String TAG = EventRequestBuilder.class.getName();

    public JSONObject build(DeviceInfo deviceInfo, String sessionId, Ad ad, EventTypes eventType, String eventName) {
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
