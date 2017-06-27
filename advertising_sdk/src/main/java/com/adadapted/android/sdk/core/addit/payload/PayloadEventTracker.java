package com.adadapted.android.sdk.core.addit.payload;

import android.util.Log;

import com.adadapted.android.sdk.core.device.DeviceInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class PayloadEventTracker {
    private final static String LOGTAG = PayloadEventTracker.class.getName();

    private final PayloadEventSink sink;
    private final JSONObject wrapper;

    public PayloadEventTracker(final DeviceInfo deviceInfo, final PayloadEventSink sink) {
        this.sink = sink;
        this.wrapper = buildPayloadTrackingWrapper(deviceInfo);
    }

    private JSONObject buildPayloadTrackingWrapper(final DeviceInfo deviceInfo) {
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

    public void trackEvent(final String payloadId,
                           final String result) {
        if(sink == null || payloadId == null || result == null) {
            Log.w(LOGTAG, "Problem with Event parameters");
            return;
        }

        try {
            final JSONObject event = new JSONObject();
            event.put("payload_id", payloadId);
            event.put("status", result);
            event.put("event_timestamp", new Date().getTime());

            final JSONArray tracking = new JSONArray();
            tracking.put(event);

            final JSONObject json = new JSONObject(wrapper.toString());
            json.put("tracking", tracking);

            sink.publishEvent(json);
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem building Payload Event JSON");
        }
    }
}
