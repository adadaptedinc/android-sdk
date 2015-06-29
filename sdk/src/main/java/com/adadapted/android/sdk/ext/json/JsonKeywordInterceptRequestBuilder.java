package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.keywordintercept.KeywordInterceptRequestBuilder;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by chrisweeden on 6/23/15.
 */
public class JsonKeywordInterceptRequestBuilder implements KeywordInterceptRequestBuilder<JSONObject> {
    private static final String TAG = JsonKeywordInterceptRequestBuilder.class.getName();

    @Override
    public JSONObject buildInitRequest(Session session, DeviceInfo deviceInfo) {
        JSONObject json = new JSONObject();

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
    public JSONObject buildTrackRequest(DeviceInfo deviceInfo,
                                        String sessionId,
                                        String term,
                                        String userInput,
                                        String eventType) {
        JSONObject json = new JSONObject();

        try {
            json.put(JsonFields.SESSIONID, sessionId);
            json.put(JsonFields.APPID, deviceInfo.getAppId());
            json.put(JsonFields.UDID, deviceInfo.getUdid());
            json.put(JsonFields.DATETIME, new Date().getTime());
            json.put(JsonFields.TERM, term);
            json.put(JsonFields.USERINPUT, userInput);
            json.put(JsonFields.EVENTTYPE, eventType);
            json.put(JsonFields.SDKVERSION, deviceInfo.getSdkVersion());
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem converting to JSON.", ex);
        }

        return json;
    }
}
