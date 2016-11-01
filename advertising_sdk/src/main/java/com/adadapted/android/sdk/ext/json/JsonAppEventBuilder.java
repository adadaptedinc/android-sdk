package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.event.AppEventBuilder;
import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

/**
 * Created by chrisweeden on 9/16/16.
 */
public class JsonAppEventBuilder implements AppEventBuilder {
    private static final String LOGTAG = JsonAppEventBuilder.class.getName();

    @Override
    public JSONObject buildWrapper(final Session session) {
        JSONObject item = new JSONObject();

        try {
            item.put(JsonFields.SESSIONID, session.getSessionId());
            item.put(JsonFields.APPID, session.getDeviceInfo().getAppId());
            item.put(JsonFields.UDID, session.getDeviceInfo().getUdid());
            item.put(JsonFields.DEVICEUDID, session.getDeviceInfo().getDeviceUdid());
            item.put(JsonFields.BUNDLEID, session.getDeviceInfo().getBundleId());
            item.put(JsonFields.BUNDLEVERSION, session.getDeviceInfo().getBundleVersion());
            item.put(JsonFields.ALLOWRETARGETING, session.getDeviceInfo().isAllowRetargetingEnabled() ? 1 : 0);
            item.put(JsonFields.OS, session.getDeviceInfo().getOs());
            item.put(JsonFields.OSV, session.getDeviceInfo().getOsv());
            item.put(JsonFields.DEVICE, session.getDeviceInfo().getDevice());
            item.put(JsonFields.CARRIER, session.getDeviceInfo().getCarrier());
            item.put(JsonFields.DW, session.getDeviceInfo().getDw());
            item.put(JsonFields.DH, session.getDeviceInfo().getDh());
            item.put(JsonFields.DENSITY, session.getDeviceInfo().getDensity().toString());
            item.put(JsonFields.TIMEZONE, session.getDeviceInfo().getTimezone());
            item.put(JsonFields.LOCALE, session.getDeviceInfo().getLocale());
            item.put(JsonFields.SDKVERSION, session.getDeviceInfo().getSdkVersion());
        }
        catch (JSONException ex) {
            Log.d(LOGTAG, "Problem converting to JSON.", ex);
        }

        return item;
    }

    @Override
    public JSONObject buildItem(final JSONObject eventWrapper,
                                final String eventSource,
                                final String eventName,
                                final Map<String, String> params) {
        try {
            final JSONObject item = new JSONObject();
            item.put(JsonFields.APP_EVENTSOURCE, eventSource);
            item.put(JsonFields.APP_EVENTNAME, eventName);
            item.put(JsonFields.APP_EVENTTIMESTAMP, new Date().getTime());
            item.put(JsonFields.APP_EVENTPARAMS, buildParams(params));

            final JSONArray items = new JSONArray();
            items.put(item);

            eventWrapper.put(JsonFields.APP_EVENTS, items);
        }
        catch (JSONException ex) {
            Log.d(LOGTAG, "Problem converting to JSON.", ex);
        }

        return eventWrapper;
    }

    private JSONObject buildParams(final Map<String, String> params) throws JSONException {
        final JSONObject p = new JSONObject();

        for(String key : params.keySet()) {
            p.put(key, params.get(key));
        }

        return p;
    }
}