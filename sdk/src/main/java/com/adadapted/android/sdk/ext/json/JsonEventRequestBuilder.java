package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.event.EventRequestBuilder;
import com.adadapted.android.sdk.core.event.model.EventTypes;
import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by chrisweeden on 4/28/15.
 */
public class JsonEventRequestBuilder implements EventRequestBuilder {
    private static final String LOGTAG = JsonEventRequestBuilder.class.getName();

    public JSONObject build(final Session session,
                            final Ad ad,
                            final EventTypes eventType,
                            final String eventName) {
        JSONObject json = new JSONObject();

        DeviceInfo deviceInfo = session.getDeviceInfo();
        String sessionId = session.getSessionId();

        try {
            json.put(JsonFields.APPID, deviceInfo.getAppId());
            json.put(JsonFields.SESSIONID, sessionId);
            json.put(JsonFields.UDID, deviceInfo.getUdid());
            json.put(JsonFields.ADID, ad.getAdId());
            json.put(JsonFields.IMPRESSIONID, ad.getImpressionId());
            json.put(JsonFields.EVENTTYPE, eventType.toString());
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
