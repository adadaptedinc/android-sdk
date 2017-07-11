package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.session.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class JsonAnomalyBuilder {
    private static final String LOGTAG = JsonAnomalyBuilder.class.getName();

    public JSONObject build(final Session session,
                            final String adId,
                            final String eventPath,
                            final String code,
                            final String message) {
        final JSONObject json = new JSONObject();

        try {
            final JSONObject ad = new JSONObject();
            ad.put("ad_id", adId);
            ad.put("event_path", eventPath);

            final JSONObject payload = new JSONObject();
            payload.put("message", message);
            payload.put("code", code);

            json.put("datetime", new Date().getTime());
            json.put("app_id", session.getDeviceInfo().getAppId());
            json.put("session_id", session.getId());
            json.put("bundle_id", session.getDeviceInfo().getBundleId());
            json.put("bundle_version", session.getDeviceInfo().getBundleVersion());
            json.put("udid", session.getDeviceInfo().getUdid());
            json.put("sdk_version", session.getDeviceInfo().getSdkVersion());
            json.put("device", session.getDeviceInfo().getDevice());
            json.put("dh", session.getDeviceInfo().getDh());
            json.put("dw", session.getDeviceInfo().getDw());
            json.put("os", session.getDeviceInfo().getOs());
            json.put("osv", session.getDeviceInfo().getOsv());
            json.put("locale", session.getDeviceInfo().getLocale());
            json.put("timezone", session.getDeviceInfo().getTimezone());
            json.put("test_mode", !session.getDeviceInfo().isProd());
            json.put("allow_retargeting", session.getDeviceInfo().isAllowRetargetingEnabled());
            json.put("payload", payload);
            json.put("ad", ad);
        }
        catch(JSONException ex) {
            Log.w(LOGTAG, "Problem generating JSON.", ex);
        }

        return json;
    }
}
