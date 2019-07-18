package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class JsonKeywordInterceptEventBuilder {
    private static final String LOGTAG = JSONException.class.getName();

    private static final String SESSION_ID = "session_id";
    private static final String APP_ID = "app_id";
    private static final String UDID = "udid";
    private static final String DATETIME = "datetime";
    private static final String SEARCH_ID = "search_id";
    private static final String TERM_ID = "term_id";
    private static final String TERM = "term";
    private static final String USER_INPUT = "user_input";
    private static final String EVENT_TYPE = "event_type";
    private static final String SDK_VERSION = "sdk_version";

    public JSONArray buildEvents(final Set<KeywordInterceptEvent> events) {
        final JSONArray jsonArray = new JSONArray();

        for(final KeywordInterceptEvent event: events) {
            try {
                jsonArray.put(buildEvent(event));
            }
            catch(JSONException ex) {
                Log.w(LOGTAG, "Problem converting to JSON.", ex);
            }
        }

        return jsonArray;
    }

    private JSONObject buildEvent(final KeywordInterceptEvent event) throws JSONException {
        final JSONObject json = new JSONObject();

        json.put(SESSION_ID, event.getSessionId());
        json.put(APP_ID, event.getAppId());
        json.put(UDID, event.getUdid());
        json.put(SEARCH_ID, event.getSearchId());
        json.put(DATETIME, event.getDatetime().getTime());
        json.put(TERM_ID, event.getTermId());
        json.put(TERM, event.getTerm());
        json.put(USER_INPUT, event.getUserInput());
        json.put(EVENT_TYPE, event.getEvent());
        json.put(SDK_VERSION, event.getSdkVersion());

        return json;
    }
}
