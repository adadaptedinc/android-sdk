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

    public JSONObject buildWrapper(final DeviceInfo deviceInfo) {
        final JSONObject errorWrapper = new JSONObject();
        try {
            errorWrapper.put(JsonFields.APPID, deviceInfo.getAppId());
            errorWrapper.put(JsonFields.UDID, deviceInfo.getUdid());
            errorWrapper.put(JsonFields.DEVICEUDID, deviceInfo.getDeviceUdid());
            errorWrapper.put(JsonFields.BUNDLEID, deviceInfo.getBundleId());
            errorWrapper.put(JsonFields.BUNDLEVERSION, deviceInfo.getBundleVersion());
            errorWrapper.put(JsonFields.ALLOWRETARGETING, deviceInfo.isAllowRetargetingEnabled() ? 1 : 0);
            errorWrapper.put(JsonFields.OS, deviceInfo.getOs());
            errorWrapper.put(JsonFields.OSV, deviceInfo.getOsv());
            errorWrapper.put(JsonFields.DEVICE, deviceInfo.getDevice());
            errorWrapper.put(JsonFields.CARRIER, deviceInfo.getCarrier());
            errorWrapper.put(JsonFields.DW, deviceInfo.getDw());
            errorWrapper.put(JsonFields.DH, deviceInfo.getDh());
            errorWrapper.put(JsonFields.DENSITY, Integer.toString(deviceInfo.getDensity()));
            errorWrapper.put(JsonFields.TIMEZONE, deviceInfo.getTimezone());
            errorWrapper.put(JsonFields.LOCALE, deviceInfo.getLocale());
            errorWrapper.put(JsonFields.SDKVERSION, deviceInfo.getSdkVersion());
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem building App Error JSON");
        }

        return errorWrapper;
    }

    public JSONObject buildItem(final JSONObject errorWrapper,
                                final Set<AppError> errors) {
        final JSONObject errorItem = new JSONObject();

        try {
            final JSONArray appErrors = new JSONArray();
            for(AppError e : errors) {
                errorItem.put(JsonFields.APP_ERRORCODE, e.getCode());
                errorItem.put(JsonFields.APP_ERRORMESSAGE, e.getMessage());
                errorItem.put(JsonFields.APP_ERRORTIMESTAMP, e.getDatetime());
                errorItem.put(JsonFields.APP_ERRORPARAMS, buildParams(e.getParams()));

                appErrors.put(errorItem);
            }

            errorWrapper.put(JsonFields.APP_ERRORS, appErrors);
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem building App Error JSON");
        }

        return errorWrapper;
    }

    private JSONObject buildParams(final Map<String, String> params) throws JSONException {
        final JSONObject p = new JSONObject();

        for(final String key : params.keySet()) {
            p.put(key, params.get(key));
        }

        return p;
    }
}
