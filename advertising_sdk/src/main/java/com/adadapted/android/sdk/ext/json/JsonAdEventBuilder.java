package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class JsonAdEventBuilder {
    private static final String LOGTAG = JsonAdEventBuilder.class.getName();

    public JSONArray buildEvents(final Set<AdEvent> events) {
        JSONArray json_arr =  new JSONArray();

        try {
            for(AdEvent e : events) {
                final JSONObject json = new JSONObject();
                json.put("app_id", e.getAppId());
                json.put("session_id", e.getSessionId());
                json.put("udid", e.getUdid());
                json.put("ad_id", e.getAdId());
                json.put("impression_id", e.getImpressionId());
                json.put("event_type", e.getEventType());
                json.put("event_name", "");
                json.put("datetime", e.getDatetime());
                json.put("sdk_version", e.getSdkVersion());

                json_arr.put(json);
            }
        }
        catch(JSONException ex) {
            Log.d(LOGTAG, "Problem parsing JSON", ex);
        }

        return json_arr;
    }
}
