package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.device.DeviceInfo;
import com.adadapted.android.sdk.core.event.AppErrorBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

/**
 * Created by chrisweeden on 10/3/16.
 */

public class JsonAppErrorBuilder implements AppErrorBuilder {
    private static final String LOGTAG = JsonAppErrorBuilder.class.getName();

    @Override
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
            errorWrapper.put(JsonFields.DENSITY, deviceInfo.getDensity().toString());
            errorWrapper.put(JsonFields.TIMEZONE, deviceInfo.getTimezone());
            errorWrapper.put(JsonFields.LOCALE, deviceInfo.getLocale());
            errorWrapper.put(JsonFields.SDKVERSION, deviceInfo.getSdkVersion());
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem building App Error JSON");
        }

        return errorWrapper;
    }

    @Override
    public JSONObject buildItem(final JSONObject errorWrapper,
                                final String errorCode,
                                final String errorMessage,
                                final Map<String, String> errorParams) {
        final JSONObject errorItem = new JSONObject();

        try {
            errorItem.put(JsonFields.APP_ERRORCODE, errorCode);
            errorItem.put(JsonFields.APP_ERRORMESSAGE, errorMessage);
            errorItem.put(JsonFields.APP_ERRORTIMESTAMP, new Date().getTime());
            errorItem.put(JsonFields.APP_ERRORPARAMS, buildParams(errorParams));

            final JSONArray appErrors = new JSONArray();
            appErrors.put(errorItem);

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
