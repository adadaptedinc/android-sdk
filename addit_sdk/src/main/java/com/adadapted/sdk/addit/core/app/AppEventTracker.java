package com.adadapted.sdk.addit.core.app;

import android.util.Log;

import com.adadapted.sdk.addit.core.device.DeviceInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

/**
 * Created by chrisweeden on 9/26/16.
 */
public class AppEventTracker {
    private static final String LOGTAG = AppEventTracker.class.getName();

    private final JSONObject appEventWrapper;
    private final DeviceInfo deviceInfo;
    private final AppEventSink sink;

    public AppEventTracker(final DeviceInfo deviceInfo,
                           final AppEventSink sink) {
        this.deviceInfo = deviceInfo;
        this.sink = sink;

        this.appEventWrapper = buildAppEventWrapper(deviceInfo);
    }

    private JSONObject buildAppEventWrapper(final DeviceInfo deviceInfo) {
        final JSONObject appEventWrapper = new JSONObject();
        try {
            appEventWrapper.put("session_id", "");
            appEventWrapper.put("app_id", deviceInfo.getAppId());
            appEventWrapper.put("udid", deviceInfo.getUdid());
            appEventWrapper.put("device_udid", deviceInfo.getDeviceUdid());
            appEventWrapper.put("bundle_id", deviceInfo.getBundleId());
            appEventWrapper.put("bundle_version", deviceInfo.getBundleVersion());
            appEventWrapper.put("allow_retargeting", deviceInfo.isAllowRetargetingEnabled() ? 1 : 0);
            appEventWrapper.put("os", deviceInfo.getOs());
            appEventWrapper.put("osv", deviceInfo.getOsv());
            appEventWrapper.put("device", deviceInfo.getDevice());
            appEventWrapper.put("carrier", deviceInfo.getCarrier());
            appEventWrapper.put("dw", deviceInfo.getDw());
            appEventWrapper.put("dh", deviceInfo.getDh());
            appEventWrapper.put("density", deviceInfo.getDensity().toString());
            appEventWrapper.put("timezone", deviceInfo.getTimezone());
            appEventWrapper.put("locale", deviceInfo.getLocale());
            appEventWrapper.put("sdk_version", deviceInfo.getSdkVersion());
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem building App Event JSON");
        }

        return appEventWrapper;
    }

    public void trackEvent(final String eventSource,
                           final String eventName,
                           final Map<String, String> eventParams) {

        try {
            final JSONObject appEventItem = new JSONObject();

            appEventItem.put("event_name", eventName);
            appEventItem.put("event_source", eventSource);
            appEventItem.put("event_timestamp", new Date().getTime());
            appEventItem.put("event_params", buildParams(eventParams));

            final JSONArray appEvents = new JSONArray();
            appEvents.put(appEventItem);

            final JSONObject json = new JSONObject(appEventWrapper.toString());
            json.put("events", appEvents);

            sink.publishEvent(json);
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem building App Event JSON");
        }
    }

    private JSONObject buildParams(final Map<String, String> params) throws JSONException {
        final JSONObject p = new JSONObject();

        for(String key : params.keySet()) {
            p.put(key, params.get(key));
        }

        return p;
    }
}
