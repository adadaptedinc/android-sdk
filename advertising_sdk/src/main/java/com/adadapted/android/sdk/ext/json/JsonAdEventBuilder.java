package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdEvent;
import com.adadapted.android.sdk.core.session.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class JsonAdEventBuilder {
    private static final String LOGTAG = JsonAdEventBuilder.class.getName();

    private static final String APP_ID = "app_id";
    private static final String SESSION_ID = "session_id";
    private static final String UDID = "udid";
    private static final String EVENTS = "events";
    private static final String SDK_VERSION = "sdk_version";

    private static final String AD_ID = "ad_id";
    private static final String IMPRESSION_ID = "impression_id";
    private static final String EVENT_TYPE = "event_type";
    private static final String CREATED_AT = "created_at";

    public JSONObject marshalEvents(final Session session, final Set<AdEvent> events) {
        final JSONObject wrapper = new JSONObject();
        try {
            wrapper.put(SESSION_ID, session.getId());
            wrapper.put(APP_ID, session.getDeviceInfo().getAppId());
            wrapper.put(UDID, session.getDeviceInfo().getUdid());
            wrapper.put(SDK_VERSION, session.getDeviceInfo().getSdkVersion());
            wrapper.put(EVENTS, buildEvents(events));
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem building Intercept Event JSON");
        }

        return wrapper;
    }

    private JSONArray buildEvents(final Set<AdEvent> events) throws JSONException {
        final JSONArray jsonArray = new JSONArray();
        for (final AdEvent event : events) {
            final JSONObject json = new JSONObject();
            json.put(AD_ID, event.getAdId());
            json.put(IMPRESSION_ID, event.getImpressionId());
            json.put(EVENT_TYPE, event.getEventType());
            json.put(CREATED_AT, event.getCreatedAt());

            jsonArray.put(json);
        }

        return jsonArray;
    }
}
