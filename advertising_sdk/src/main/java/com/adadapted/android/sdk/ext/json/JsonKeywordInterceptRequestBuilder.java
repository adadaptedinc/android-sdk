package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.session.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class JsonKeywordInterceptRequestBuilder {
    private static final String TAG = JsonKeywordInterceptRequestBuilder.class.getName();

    public JSONObject buildInitRequest(final Session session) {
        final JSONObject json = new JSONObject();
        final DeviceInfo deviceInfo = session.getDeviceInfo();

        try {
            json.put(JsonFields.SESSIONID, session.getId());
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
}
