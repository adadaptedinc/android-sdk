package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.anomaly.AnomalyBuilder;
import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by chrisweeden on 9/23/16.
 */
public class JsonAnomalyBuilder implements AnomalyBuilder {
    private static final String LOGTAG = JsonAnomalyBuilder.class.getName();

    @Override
    public JSONObject build(final Session session,
                            final String adId,
                            final String eventPath,
                            final String code,
                            final String message) {
        final JSONObject json = new JSONObject();

        try {
            json.put("datetime", new Date().getTime());
            json.put("app_id", session.getDeviceInfo().getAppId());
            json.put("session_id", session.getSessionId());
            json.put("bundle_id", session.getDeviceInfo().getBundleId());
            json.put("bundle_version", session.getDeviceInfo().getBundleVersion());
            json.put("udid", session.getDeviceInfo().getUdid());
            json.put("sdk_version", session.getDeviceInfo().getSdkVersion());
            json.put("os", session.getDeviceInfo().getOs());
            json.put("osv", session.getDeviceInfo().getOsv());
            json.put("message", message);
            json.put("code", code);
            json.put("ad_id", adId);
            json.put("event_path", eventPath);
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem generating JSON.", ex);
        }

        return json;
    }
}
