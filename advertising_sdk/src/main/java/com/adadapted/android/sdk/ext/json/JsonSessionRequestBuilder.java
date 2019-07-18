package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.device.DeviceInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class JsonSessionRequestBuilder {
    private static final String LOGTAG = JsonSessionRequestBuilder.class.getName();

    private static final String APP_ID = "app_id";
    private static final String UDID = "udid";
    private static final String BUNDLE_ID = "bundle_id";
    private static final String BUNDLE_VERSION = "bundle_version";
    private static final String DEVICE_NAME = "device_name";
    private static final String DEVICE_UDID = "device_udid";
    private static final String DEVICE_OS = "device_os";
    private static final String DEVICE_OSV = "device_osv";
    private static final String DEVICE_LOCALE = "device_locale";
    private static final String DEVICE_TIMEZONE = "device_timezone";
    private static final String DEVICE_CARRIER = "device_carrier";
    private static final String DEVICE_HEIGHT = "device_height";
    private static final String DEVICE_WIDTH = "device_width";
    private static final String DEVICE_DENSITY = "device_density";
    private static final String ALLOW_RETARGETING = "allow_retargeting";
    private static final String CREATED_AT = "created_at";
    private static final String SDK_VERSION = "sdk_version";
    private static final String PARAMS = "params";

    public JSONObject buildSessionInitRequest(final DeviceInfo deviceInfo) {
        final JSONObject json = new JSONObject();

        try {
            json.put(APP_ID, deviceInfo.getAppId());
            json.put(UDID, deviceInfo.getUdid());
            json.put(BUNDLE_ID, deviceInfo.getBundleId());
            json.put(BUNDLE_VERSION, deviceInfo.getBundleVersion());
            json.put(DEVICE_NAME, deviceInfo.getDevice());
            json.put(DEVICE_UDID, deviceInfo.getDeviceUdid());
            json.put(DEVICE_OS, deviceInfo.getOs());
            json.put(DEVICE_OSV, deviceInfo.getOsv());
            json.put(DEVICE_LOCALE, deviceInfo.getLocale());
            json.put(DEVICE_TIMEZONE, deviceInfo.getTimezone());
            json.put(DEVICE_CARRIER, deviceInfo.getCarrier());
            json.put(DEVICE_HEIGHT, deviceInfo.getDh());
            json.put(DEVICE_WIDTH, deviceInfo.getDw());
            json.put(DEVICE_DENSITY, Integer.valueOf(deviceInfo.getDensity()).toString());
            json.put(ALLOW_RETARGETING, deviceInfo.isAllowRetargetingEnabled());
            json.put(CREATED_AT, new Date().getTime());
            json.put(SDK_VERSION, deviceInfo.getSdkVersion());
            json.put(PARAMS, new JSONObject(deviceInfo.getParams()));
        }
        catch(JSONException ex) {
            Log.d(LOGTAG, "Problem converting to JSON.", ex);
        }

        return json;
    }
}
