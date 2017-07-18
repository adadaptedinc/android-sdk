package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.addit.PayloadEvent;
import com.adadapted.android.sdk.core.device.DeviceInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import static com.google.ads.AdRequest.LOGTAG;

public class JsonPayloadBuilder {
    private JSONObject wrapper;

    public JsonPayloadBuilder() {
        wrapper = new JSONObject();
    }

    public JSONObject buildRequest(final DeviceInfo deviceInfo) {
        this.wrapper = buildEventWrapper(deviceInfo);

        final JSONObject request = new JSONObject();
        try {
            request.put("app_id", deviceInfo.getAppId());
            request.put("udid", deviceInfo.getUdid());
            request.put("bundle_id", deviceInfo.getBundleId());
            request.put("bundle_version", deviceInfo.getBundleVersion());
            request.put("os", deviceInfo.getOs());
            request.put("osv", deviceInfo.getOsv());
            request.put("device", deviceInfo.getDevice());
            request.put("sdk_version", deviceInfo.getSdkVersion());
            request.put("timestamp", new Date().getTime());
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem building App Event JSON");
        }

        return request;
    }

    private JSONObject buildEventWrapper(final DeviceInfo deviceInfo) {
        final JSONObject wrapper = new JSONObject();

        if(deviceInfo != null) {
            try {
                wrapper.put("app_id", deviceInfo.getAppId());
                wrapper.put("udid", deviceInfo.getUdid());
                wrapper.put("bundle_id", deviceInfo.getBundleId());
                wrapper.put("bundle_version", deviceInfo.getBundleVersion());
                wrapper.put("os", deviceInfo.getOs());
                wrapper.put("osv", deviceInfo.getOsv());
                wrapper.put("device", deviceInfo.getDevice());
                wrapper.put("sdk_version", deviceInfo.getSdkVersion());
            } catch (JSONException ex) {
                Log.w(LOGTAG, "Problem building Payload Tracking Wrapper JSON");
            }
        }

        return wrapper;
    }

    public JSONObject buildEvent(final PayloadEvent event) {
        try {
            final JSONObject evt = new JSONObject();
            evt.put("payload_id", event.getPayloadId());
            evt.put("status", event.getStatus());
            evt.put("event_timestamp", event.getTimestamp());

            final JSONArray tracking = new JSONArray();
            tracking.put(evt);

            final JSONObject json = new JSONObject(wrapper.toString());
            json.put("tracking", tracking);

            return json;

        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem building Payload Event JSON");
        }

        return new JSONObject();
    }
}
