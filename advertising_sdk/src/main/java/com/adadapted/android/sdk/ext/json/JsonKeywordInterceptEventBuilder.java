package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class JsonKeywordInterceptEventBuilder {
    private static final String LOGTAG = JSONException.class.getName();

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

        json.put(JsonFields.SESSIONID, event.getSessionId());
        json.put(JsonFields.APPID, event.getAppId());
        json.put(JsonFields.UDID, event.getUdid());
        json.put(JsonFields.SEARCHID, event.getSearchId());
        json.put(JsonFields.DATETIME, event.getDatetime().getTime());
        json.put(JsonFields.TERMID, event.getTermId());
        json.put(JsonFields.TERM, event.getTerm());
        json.put(JsonFields.USERINPUT, event.getUserInput());
        json.put(JsonFields.EVENTTYPE, event.getEvent());
        json.put(JsonFields.SDKVERSION, event.getSdkVersion());

        return json;
    }
}
