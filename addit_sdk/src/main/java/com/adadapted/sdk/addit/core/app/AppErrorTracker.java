package com.adadapted.sdk.addit.core.app;

import android.util.Log;

import com.adadapted.sdk.addit.core.device.DeviceInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

/**
 * Created by chrisweeden on 9/29/16.
 */

public class AppErrorTracker {
    private static final String LOGTAG = AppErrorTracker.class.getName();

    private final JSONObject appEventWrapper;
    private final AppErrorSink sink;

    public AppErrorTracker(final DeviceInfo deviceInfo,
                           final AppErrorSink sink) {
        this.sink = sink;
        this.appEventWrapper = buildAppEventWrapper(deviceInfo);
    }

    private JSONObject buildAppEventWrapper(final DeviceInfo deviceInfo) {
        final JSONObject errorWrapper = new JSONObject();
        try {
            errorWrapper.put("session_id", "");
            errorWrapper.put("app_id", deviceInfo.getAppId());
            errorWrapper.put("udid", deviceInfo.getUdid());
            errorWrapper.put("device_udid", deviceInfo.getDeviceUdid());
            errorWrapper.put("bundle_id", deviceInfo.getBundleId());
            errorWrapper.put("bundle_version", deviceInfo.getBundleVersion());
            errorWrapper.put("allow_retargeting", deviceInfo.isAllowRetargetingEnabled() ? 1 : 0);
            errorWrapper.put("os", deviceInfo.getOs());
            errorWrapper.put("osv", deviceInfo.getOsv());
            errorWrapper.put("device", deviceInfo.getDevice());
            errorWrapper.put("carrier", deviceInfo.getCarrier());
            errorWrapper.put("dw", deviceInfo.getDw());
            errorWrapper.put("dh", deviceInfo.getDh());
            errorWrapper.put("density", deviceInfo.getDensity().toString());
            errorWrapper.put("timezone", deviceInfo.getTimezone());
            errorWrapper.put("locale", deviceInfo.getLocale());
            errorWrapper.put("sdk_version", deviceInfo.getSdkVersion());
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem building App Error JSON");
        }

        return errorWrapper;
    }

    public void trackError(final String errorCode,
                           final String errorMessage,
                           final Map<String, String> errorParams) {

        try {
            final JSONObject errorItem = new JSONObject();

            errorItem.put("error_code", errorCode);
            errorItem.put("error_message", errorMessage);
            errorItem.put("error_timestamp", new Date().getTime());
            errorItem.put("error_params", buildParams(errorParams));

            final JSONArray appEvents = new JSONArray();
            appEvents.put(errorItem);

            final JSONObject json = new JSONObject(appEventWrapper.toString());
            json.put("errors", appEvents);

            sink.publishError(json);
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem building App Error JSON");
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
