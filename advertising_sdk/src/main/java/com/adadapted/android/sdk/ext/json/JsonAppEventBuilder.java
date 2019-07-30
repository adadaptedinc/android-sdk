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

    private static final String APP_ID = "app_id";
    private static final String UDID = "udid";
    private static final String BUNDLE_ID = "bundle_id";
    private static final String BUNDLE_VERSION = "bundle_version";
    private static final String DEVICE = "device";
    private static final String DEVICE_UDID = "device_udid";
    private static final String OS = "os";
    private static final String OSV = "osv";
    private static final String LOCALE = "locale";
    private static final String TIMEZONE = "timezone";
    private static final String CARRIER = "carrier";
    private static final String DH = "dh";
    private static final String DW = "dw";
    private static final String DENSITY = "density";
    private static final String ALLOW_RETARGETING = "allow_retargeting";
    private static final String SDK_VERSION = "sdk_version";

    private static final String APP_EVENTS = "events";
    private static final String APP_EVENT_SOURCE = "event_source";
    private static final String APP_EVENT_NAME = "event_name";
    private static final String APP_EVENT_TIMESTAMP = "event_timestamp";
    private static final String APP_EVENT_PARAMS = "event_params";

    public JSONObject buildWrapper(final DeviceInfo deviceInfo) {
        final JSONObject wrapper = new JSONObject();

        try {
            wrapper.put(APP_ID, deviceInfo.getAppId());
            wrapper.put(UDID, deviceInfo.getUdid());
            wrapper.put(DEVICE_UDID, deviceInfo.getDeviceUdid());
            wrapper.put(BUNDLE_ID, deviceInfo.getBundleId());
            wrapper.put(BUNDLE_VERSION, deviceInfo.getBundleVersion());
            wrapper.put(ALLOW_RETARGETING, deviceInfo.isAllowRetargetingEnabled() ? 1 : 0);
            wrapper.put(OS, deviceInfo.getOs());
            wrapper.put(OSV, deviceInfo.getOsv());
            wrapper.put(DEVICE, deviceInfo.getDevice());
            wrapper.put(CARRIER, deviceInfo.getCarrier());
            wrapper.put(DW, deviceInfo.getDw());
            wrapper.put(DH, deviceInfo.getDh());
            wrapper.put(DENSITY, Integer.toString(deviceInfo.getDensity()));
            wrapper.put(TIMEZONE, deviceInfo.getTimezone());
            wrapper.put(LOCALE, deviceInfo.getLocale());
            wrapper.put(SDK_VERSION, deviceInfo.getSdkVersion());
        }
        catch (JSONException ex) {
            Log.d(LOGTAG, "Problem converting to JSON.", ex);
        }

        return wrapper;
    }

    public JSONObject buildItem(final JSONObject wrapper,
                                final Set<AppEvent> events) {
        try {
            final JSONArray items = new JSONArray();

            for(AppEvent event : events) {
                final JSONObject item = new JSONObject();
                item.put(APP_EVENT_SOURCE, event.getType());
                item.put(APP_EVENT_NAME, event.getName());
                item.put(APP_EVENT_TIMESTAMP, event.getDatetime());
                item.put(APP_EVENT_PARAMS, buildParams(event.getParams()));

                items.put(item);
            }

            wrapper.put(APP_EVENTS, items);
        }
        catch (JSONException ex) {
            Log.d(LOGTAG, "Problem converting to JSON.", ex);
        }

        return wrapper;
    }

    private JSONObject buildParams(final Map<String, String> params) throws JSONException {
        final JSONObject p = new JSONObject();

        for(final String key : params.keySet()) {
            p.put(key, params.get(key));
        }

        return p;
    }
}
