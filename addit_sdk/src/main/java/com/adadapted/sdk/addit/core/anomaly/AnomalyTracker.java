package com.adadapted.sdk.addit.core.anomaly;

import android.util.Log;

import com.adadapted.sdk.addit.core.device.DeviceInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 *
 */
public class AnomalyTracker {
    private static final String LOGTAG = AnomalyTracker.class.getName();

    private final DeviceInfo deviceInfo;
    private final AnomalySink sink;

    public AnomalyTracker(final DeviceInfo deviceInfo,
                          final AnomalySink sink) {
        this.deviceInfo = deviceInfo;
        this.sink = sink;
    }

    public void trackAnomaly(final String eventPath,
                             final String code,
                             final String message) {
        final JSONObject anomalyJson = new JSONObject();

        try {
            final JSONObject ad = new JSONObject();
            ad.put("ad_id", "");
            ad.put("event_path", eventPath);

            final JSONObject payload = new JSONObject();
            payload.put("message", message);
            payload.put("code", code);

            anomalyJson.put("datetime", new Date().getTime());
            anomalyJson.put("app_id", deviceInfo.getAppId());
            anomalyJson.put("session_id", "");
            anomalyJson.put("bundle_id", deviceInfo.getBundleId());
            anomalyJson.put("bundle_version", deviceInfo.getBundleVersion());
            anomalyJson.put("udid", deviceInfo.getUdid());
            anomalyJson.put("sdk_version", deviceInfo.getSdkVersion());
            anomalyJson.put("device", deviceInfo.getDevice());
            anomalyJson.put("dh", deviceInfo.getDh());
            anomalyJson.put("dw", deviceInfo.getDw());
            anomalyJson.put("os", deviceInfo.getOs());
            anomalyJson.put("osv", deviceInfo.getOsv());
            anomalyJson.put("locale", deviceInfo.getLocale());
            anomalyJson.put("timezone", deviceInfo.getTimezone());
            anomalyJson.put("test_mode", !deviceInfo.isProd());
            anomalyJson.put("allow_retargeting", deviceInfo.isAllowRetargetingEnabled());
            anomalyJson.put("payload", payload);
            anomalyJson.put("ad", ad);

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(anomalyJson);

            sink.publishAnomaly(jsonArray);
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem building Anomaly JSON");
        }
    }
}
