package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class JsonAdEventBuilder {
    private static final String LOGTAG = JsonAdEventBuilder.class.getName();

    private static final String APP_ID = "app_id";
    private static final String SESSION_ID = "session_id";
    private static final String UDID = "udid";
    private static final String AD_ID = "ad_id";
    private static final String IMPRESSION_ID = "impression_id";
    private static final String EVENT_TYPE = "event_type";
    private static final String DATETIME = "datetime";
    private static final String SDK_VERSION = "sdk_version";

    public JSONArray buildEvents(final Set<AdEvent> events) {
        final JSONArray arr =  new JSONArray();

        try {
            for(final AdEvent event : events) {
                final JSONObject json = new JSONObject();
                json.put(APP_ID, event.getAppId());
                json.put(SESSION_ID, event.getSessionId());
                json.put(UDID, event.getUdid());
                json.put(AD_ID, event.getAdId());
                json.put(IMPRESSION_ID, event.getImpressionId());
                json.put(EVENT_TYPE, event.getEventType());
                json.put(DATETIME, event.getDatetime());
                json.put(SDK_VERSION, event.getSdkVersion());

                arr.put(json);
            }
        }
        catch(JSONException ex) {
            Log.d(LOGTAG, "Problem parsing JSON", ex);
        }

        return arr;
    }
}
