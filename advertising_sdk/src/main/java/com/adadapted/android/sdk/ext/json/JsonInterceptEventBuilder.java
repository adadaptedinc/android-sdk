package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.intercept.InterceptEvent;
import com.adadapted.android.sdk.core.session.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class JsonInterceptEventBuilder {
    private static final String LOGTAG = JSONException.class.getName();

    private static final String SESSION_ID = "session_id";
    private static final String APP_ID = "app_id";
    private static final String UDID = "udid";
    private static final String EVENTS = "events";
    private static final String SDK_VERSION = "sdk_version";

    private static final String SEARCH_ID = "search_id";
    private static final String TERM_ID = "term_id";
    private static final String TERM = "term";
    private static final String USER_INPUT = "user_input";
    private static final String EVENT_TYPE = "event_type";
    private static final String CREATED_AT = "created_at";

    public JSONObject marshalEvents(final Session session, final Set<InterceptEvent> events) {
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

    private JSONArray buildEvents(final Set<InterceptEvent> events) throws JSONException {
        final JSONArray jsonArray = new JSONArray();
        for (final InterceptEvent event : events) {
            final JSONObject json = new JSONObject();
            json.put(SEARCH_ID, event.getSearchId());
            json.put(CREATED_AT, event.getCreatedAt().getTime());
            json.put(TERM_ID, event.getTermId());
            json.put(TERM, event.getTerm());
            json.put(USER_INPUT, event.getUserInput());
            json.put(EVENT_TYPE, event.getEvent());

            jsonArray.put(json);
        }

        return jsonArray;
    }
}
