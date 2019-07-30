package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.addit.PayloadEvent;
import com.adadapted.android.sdk.core.device.DeviceInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class JsonPayloadBuilder {
    private static final String LOGTAG = JsonPayloadBuilder.class.getName();

    private static final String APP_ID = "app_id";
    private static final String UDID = "udid";
    private static final String BUNDLE_ID = "bundle_id";
    private static final String BUNDLE_VERSION = "bundle_version";
    private static final String DEVICE = "device";
    private static final String OS = "os";
    private static final String OSV = "osv";
    private static final String TIMESTAMP = "timestamp";
    private static final String SDK_VERSION = "sdk_version";

    private static final String PAYLOAD_ID = "payload_id";
    private static final String STATUS = "status";
    private static final String EVENT_TIMESTAMP = "event_timestamp";
    private static final String TRACKING = "tracking";

    private JSONObject wrapper;

    public JsonPayloadBuilder() {
        wrapper = new JSONObject();
    }

    public JSONObject buildRequest(final DeviceInfo deviceInfo) {
        this.wrapper = buildEventWrapper(deviceInfo);

        final JSONObject request = new JSONObject();
        try {
            request.put(APP_ID, deviceInfo.getAppId());
            request.put(UDID, deviceInfo.getUdid());
            request.put(BUNDLE_ID, deviceInfo.getBundleId());
            request.put(BUNDLE_VERSION, deviceInfo.getBundleVersion());
            request.put(OS, deviceInfo.getOs());
            request.put(OSV, deviceInfo.getOsv());
            request.put(DEVICE, deviceInfo.getDevice());
            request.put(SDK_VERSION, deviceInfo.getSdkVersion());
            request.put(TIMESTAMP, new Date().getTime());
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem building App Event JSON", ex);
        }

        return request;
    }

    private JSONObject buildEventWrapper(final DeviceInfo deviceInfo) {
        final JSONObject wrapper = new JSONObject();

        if(deviceInfo != null) {
            try {
                wrapper.put(APP_ID, deviceInfo.getAppId());
                wrapper.put(UDID, deviceInfo.getUdid());
                wrapper.put(BUNDLE_ID, deviceInfo.getBundleId());
                wrapper.put(BUNDLE_VERSION, deviceInfo.getBundleVersion());
                wrapper.put(OS, deviceInfo.getOs());
                wrapper.put(OSV, deviceInfo.getOsv());
                wrapper.put(DEVICE, deviceInfo.getDevice());
                wrapper.put(SDK_VERSION, deviceInfo.getSdkVersion());
            } catch (JSONException ex) {
                Log.w(LOGTAG, "Problem building Payload Tracking Wrapper JSON", ex);
            }
        }

        return wrapper;
    }

    public JSONObject buildEvent(final PayloadEvent event) {
        try {
            final JSONObject evt = new JSONObject();
            evt.put(PAYLOAD_ID, event.getPayloadId());
            evt.put(STATUS, event.getStatus());
            evt.put(EVENT_TIMESTAMP, event.getTimestamp());

            final JSONArray tracking = new JSONArray();
            tracking.put(evt);

            final JSONObject json = new JSONObject(wrapper.toString());
            json.put(TRACKING, tracking);

            return json;

        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem building Payload Event JSON", ex);
        }

        return new JSONObject();
    }
}
