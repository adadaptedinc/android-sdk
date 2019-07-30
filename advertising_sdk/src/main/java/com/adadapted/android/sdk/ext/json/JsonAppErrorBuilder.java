package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.event.AppError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

public class JsonAppErrorBuilder {
    private static final String LOGTAG = JsonAppErrorBuilder.class.getName();

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

    private static final String APP_ERRORS = "errors";
    private static final String APP_ERROR_CODE = "error_code";
    private static final String APP_ERROR_MESSAGE = "error_message";
    private static final String APP_ERROR_TIMESTAMP = "error_timestamp";
    private static final String APP_ERROR_PARAMS = "error_params";

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
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem building App Error JSON");
        }

        return wrapper;
    }

    public JSONObject buildItem(final JSONObject wrapper,
                                final Set<AppError> errors) {
        final JSONObject item = new JSONObject();

        try {
            final JSONArray json = new JSONArray();
            for(final AppError error : errors) {
                item.put(APP_ERROR_CODE, error.getCode());
                item.put(APP_ERROR_MESSAGE, error.getMessage());
                item.put(APP_ERROR_TIMESTAMP, error.getDatetime());
                item.put(APP_ERROR_PARAMS, buildParams(error.getParams()));

                json.put(item);
            }

            wrapper.put(APP_ERRORS, json);
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem building App Error JSON");
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
