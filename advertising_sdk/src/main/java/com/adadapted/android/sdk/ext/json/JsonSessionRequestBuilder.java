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
    private static final String DEVICE = "device";
    private static final String DEVICE_UDID = "device_udid";
    private static final String OS = "os";
    private static final String OSV = "osv";
    private static final String LOCALE = "locale";
    private static final String TIMEZONE = "timezone";
    private static final String DATETIME = "datetime";
    private static final String CARRIER = "carrier";
    private static final String DH = "dh";
    private static final String DW = "dw";
    private static final String DENSITY = "density";
    private static final String ALLOW_RETARGETING = "allow_retargeting";
    private static final String SDK_VERSION = "sdk_version";
    private static final String PARAMS = "init_params";

    public JSONObject buildSessionInitRequest(final DeviceInfo deviceInfo) {
        final JSONObject json = new JSONObject();

        try {
            json.put(APP_ID, deviceInfo.getAppId());
            json.put(UDID, deviceInfo.getUdid());
            json.put(BUNDLE_ID, deviceInfo.getBundleId());
            json.put(BUNDLE_VERSION, deviceInfo.getBundleVersion());
            json.put(DEVICE, deviceInfo.getDevice());
            json.put(DEVICE_UDID, deviceInfo.getDeviceUdid());
            json.put(OS, deviceInfo.getOs());
            json.put(OSV, deviceInfo.getOsv());
            json.put(LOCALE, deviceInfo.getLocale());
            json.put(TIMEZONE, deviceInfo.getTimezone());
            json.put(CARRIER, deviceInfo.getCarrier());
            json.put(DH, deviceInfo.getDh());
            json.put(DW, deviceInfo.getDw());
            json.put(DENSITY, deviceInfo.getDensity());
            json.put(DATETIME, new Date().getTime());
            json.put(ALLOW_RETARGETING, deviceInfo.isAllowRetargetingEnabled() ? 1 : 0);
            json.put(SDK_VERSION, deviceInfo.getSdkVersion());
            json.put(PARAMS, new JSONObject(deviceInfo.getParams()));
        }
        catch(JSONException ex) {
            Log.d(LOGTAG, "Problem converting to JSON.", ex);
        }

        return json;
    }
}
