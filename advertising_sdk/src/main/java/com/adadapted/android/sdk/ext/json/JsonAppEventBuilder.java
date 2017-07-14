package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.event.AppEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

public class JsonAppEventBuilder {
    private static final String LOGTAG = JsonAppEventBuilder.class.getName();

    public JSONObject buildWrapper(final DeviceInfo deviceInfo) {
        JSONObject eventWrapper = new JSONObject();

        try {
            eventWrapper.put(JsonFields.APPID, deviceInfo.getAppId());
            eventWrapper.put(JsonFields.UDID, deviceInfo.getUdid());
            eventWrapper.put(JsonFields.DEVICEUDID, deviceInfo.getDeviceUdid());
            eventWrapper.put(JsonFields.BUNDLEID, deviceInfo.getBundleId());
            eventWrapper.put(JsonFields.BUNDLEVERSION, deviceInfo.getBundleVersion());
            eventWrapper.put(JsonFields.ALLOWRETARGETING, deviceInfo.isAllowRetargetingEnabled() ? 1 : 0);
            eventWrapper.put(JsonFields.OS, deviceInfo.getOs());
            eventWrapper.put(JsonFields.OSV, deviceInfo.getOsv());
            eventWrapper.put(JsonFields.DEVICE, deviceInfo.getDevice());
            eventWrapper.put(JsonFields.CARRIER, deviceInfo.getCarrier());
            eventWrapper.put(JsonFields.DW, deviceInfo.getDw());
            eventWrapper.put(JsonFields.DH, deviceInfo.getDh());
            eventWrapper.put(JsonFields.DENSITY, Integer.toString(deviceInfo.getDensity()));
            eventWrapper.put(JsonFields.TIMEZONE, deviceInfo.getTimezone());
            eventWrapper.put(JsonFields.LOCALE, deviceInfo.getLocale());
            eventWrapper.put(JsonFields.SDKVERSION, deviceInfo.getSdkVersion());
        }
        catch (JSONException ex) {
            Log.d(LOGTAG, "Problem converting to JSON.", ex);
        }

        return eventWrapper;
    }

    public JSONObject buildItem(final JSONObject eventWrapper,
                                final Set<AppEvent> events) {
        try {
            final JSONArray items = new JSONArray();

            for(AppEvent e : events) {
                final JSONObject item = new JSONObject();
                item.put(JsonFields.APP_EVENTSOURCE, e.getType());
                item.put(JsonFields.APP_EVENTNAME, e.getName());
                item.put(JsonFields.APP_EVENTTIMESTAMP, e.getDatetime());
                item.put(JsonFields.APP_EVENTPARAMS, buildParams(e.getParams()));

                items.put(item);
            }

            eventWrapper.put(JsonFields.APP_EVENTS, items);
        }
        catch (JSONException ex) {
            Log.d(LOGTAG, "Problem converting to JSON.", ex);
        }

        return eventWrapper;
    }

    private JSONObject buildParams(final Map<String, String> params) throws JSONException {
        final JSONObject p = new JSONObject();

        for(final String key : params.keySet()) {
            p.put(key, params.get(key));
        }

        return p;
    }
}
