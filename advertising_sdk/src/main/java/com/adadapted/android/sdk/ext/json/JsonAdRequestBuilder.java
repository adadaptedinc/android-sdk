package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.AdRequestBuilder;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by chrisweeden on 4/16/15.
 */
public class JsonAdRequestBuilder implements AdRequestBuilder {
    private static final String LOGTAG = JsonAdRequestBuilder.class.getName();

    public JSONObject buildAdRequest(final Session session) {
        final JSONObject json = new JSONObject();
        final DeviceInfo deviceInfo = session.getDeviceInfo();

        try {
            json.put(JsonFields.APPID, deviceInfo.getAppId());
            json.put(JsonFields.UDID, deviceInfo.getUdid());
            json.put(JsonFields.SESSIONID, session.getSessionId());
            json.put(JsonFields.DATETIME, new Date().getTime());
            json.put(JsonFields.SDKVERSION, deviceInfo.getSdkVersion());
        }
        catch(JSONException ex) {
            Log.d(LOGTAG, "Problem parsing JSON", ex);
        }

        return json;
    }
}
