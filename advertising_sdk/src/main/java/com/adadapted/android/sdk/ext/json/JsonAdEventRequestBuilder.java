package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.ad.AdEventRequestBuilder;
import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by chrisweeden on 4/28/15.
 */
public class JsonAdEventRequestBuilder implements AdEventRequestBuilder {
    private static final String LOGTAG = JsonAdEventRequestBuilder.class.getName();

    public JSONObject build(final Session session,
                            final Ad ad,
                            final String eventType,
                            final String eventName) {
        final JSONObject json = new JSONObject();

        final DeviceInfo deviceInfo = session.getDeviceInfo();
        final String sessionId = session.getSessionId();

        try {
            json.put(JsonFields.APPID, deviceInfo.getAppId());
            json.put(JsonFields.SESSIONID, sessionId);
            json.put(JsonFields.UDID, deviceInfo.getUdid());
            json.put(JsonFields.ADID, ad.getAdId());
            json.put(JsonFields.IMPRESSIONID, ad.getImpressionId());
            json.put(JsonFields.EVENTTYPE, eventType);
            json.put(JsonFields.EVENTNAME, eventName);
            json.put(JsonFields.DATETIME, new Date().getTime());
            json.put(JsonFields.SDKVERSION, deviceInfo.getSdkVersion());
        }
        catch(JSONException ex) {
            Log.d(LOGTAG, "Problem converting to JSON.", ex);
        }

        return json;
    }
}
