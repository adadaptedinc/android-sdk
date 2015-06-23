package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.session.SessionRequestBuilder;
import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by chrisweeden on 4/28/15.
 */
public class JsonSessionRequestBuilder implements SessionRequestBuilder {
    private static final String TAG = JsonSessionRequestBuilder.class.getName();

    public JSONObject buildSessionInitRequest(DeviceInfo deviceInfo) {
        JSONObject json = new JSONObject();

        try {
            json = buildBaseSessionRequest(deviceInfo);
        }
        catch(JSONException ex) {
            Log.d(TAG, "Problem converting to JSON.", ex);
        }

        return json;
    }

    public JSONObject buildSessionReinitRequest(DeviceInfo deviceInfo, Session session) {
        JSONObject json = new JSONObject();

        try {
            json = buildBaseSessionRequest(deviceInfo);
            json.put(JsonFields.SESSIONID, session.getSessionId());
        }
        catch(JSONException ex) {
            Log.d(TAG, "Problem converting to JSON.", ex);
        }

        return json;
    }

    private JSONObject buildBaseSessionRequest(DeviceInfo deviceInfo) throws JSONException {
        JSONObject json = new JSONObject();

        json.put(JsonFields.APPID, deviceInfo.getAppId());
        json.put(JsonFields.UDID, deviceInfo.getUdid());
        json.put(JsonFields.BUNDLEID, deviceInfo.getBundleId());
        json.put(JsonFields.DEVICE, deviceInfo.getDevice());
        json.put(JsonFields.OS, deviceInfo.getOs());
        json.put(JsonFields.OSV, deviceInfo.getOsv());
        json.put(JsonFields.LOCALE, deviceInfo.getLocale());
        json.put(JsonFields.TIMEZONE, deviceInfo.getTimezone());
        json.put(JsonFields.CARRIER, deviceInfo.getCarrier());
        json.put(JsonFields.DH, deviceInfo.getDh());
        json.put(JsonFields.DW, deviceInfo.getDw());
        json.put(JsonFields.DENSITY, deviceInfo.getDensity());
        json.put(JsonFields.DATETIME, new Date().getTime());
        json.put(JsonFields.ALLOWRETARGETING, 1);
        json.put(JsonFields.SDKVERSION, deviceInfo.getSdkVersion());

        String[] zones = deviceInfo.getZones();

        JSONArray zonesArray = new JSONArray();
        for(String zone : zones) {
            zonesArray.put(zone);
        }

        json.put(JsonFields.ZONES, zonesArray);

        return json;
    }
}
