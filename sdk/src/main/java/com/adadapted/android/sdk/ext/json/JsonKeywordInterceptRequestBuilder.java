package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptRequestBuilder;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.keywordintercept.model.KeywordInterceptEvent;
import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Set;

/**
 * Created by chrisweeden on 6/23/15.
 */
public class JsonKeywordInterceptRequestBuilder implements KeywordInterceptRequestBuilder {
    private static final String TAG = JsonKeywordInterceptRequestBuilder.class.getName();

    @Override
    public JSONObject buildInitRequest(Session session) {
        JSONObject json = new JSONObject();
        DeviceInfo deviceInfo = session.getDeviceInfo();

        try {
            json.put(JsonFields.SESSIONID, session.getSessionId());
            json.put(JsonFields.APPID, deviceInfo.getAppId());
            json.put(JsonFields.UDID, deviceInfo.getUdid());
            json.put(JsonFields.DATETIME, new Date().getTime());
            json.put(JsonFields.SDKVERSION, deviceInfo.getSdkVersion());
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem converting to JSON.", ex);
        }

        return json;
    }

    @Override
    public JSONArray buildTrackRequest(Set<KeywordInterceptEvent> events) {
        JSONArray jsonArray = new JSONArray();

        for(KeywordInterceptEvent event: events) {
            JSONObject json = new JSONObject();

            try {
                json.put(JsonFields.SESSIONID, event.getSessionId());
                json.put(JsonFields.APPID, event.getAppId());
                json.put(JsonFields.UDID, event.getUdid());
                json.put(JsonFields.SEARCHID, event.getSearchId());
                json.put(JsonFields.DATETIME, event.getDatetime().getTime());
                json.put(JsonFields.TERM, event.getTerm());
                json.put(JsonFields.USERINPUT, event.getUserInput());
                json.put(JsonFields.EVENTTYPE, event.getEvent());
                json.put(JsonFields.SDKVERSION, event.getSdkVersion());
            }
            catch(JSONException ex) {
                Log.w(TAG, "Problem converting to JSON.", ex);
            }

            jsonArray.put(json);
        }

        return jsonArray;
    }
}
