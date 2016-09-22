package com.adadapted.android.sdk.ext.json;

import android.util.Log;

import com.adadapted.android.sdk.core.event.AppEventBuilder;
import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Created by chrisweeden on 9/16/16.
 */
public class JsonAppEventBuilder implements AppEventBuilder {
    private static final String LOGTAG = JsonAppEventBuilder.class.getName();

    @Override
    public JSONObject build(final Session session,
                            final Set<JSONObject> events) {
        JSONObject item = new JSONObject();

        try {
            item.put("session_id", session.getSessionId());
            item.put("app_id", session.getDeviceInfo().getAppId());
            item.put("udid", session.getDeviceInfo().getUdid());
            item.put("device_udid", session.getDeviceInfo().getDeviceUdid());
            item.put("bundle_id", session.getDeviceInfo().getBundleId());
            item.put("bundle_version", session.getDeviceInfo().getBundleVersion());
            item.put("allow_retargeting", session.getDeviceInfo().allowRetargetingEnabled() ? 1 : 0);
            item.put("os", session.getDeviceInfo().getOs());
            item.put("osv", session.getDeviceInfo().getOsv());
            item.put("device", session.getDeviceInfo().getDevice());
            item.put("carrier", session.getDeviceInfo().getCarrier());
            item.put("dw", session.getDeviceInfo().getDw());
            item.put("dh", session.getDeviceInfo().getDh());
            item.put("density", session.getDeviceInfo().getDensity().toString());
            item.put("timezone", session.getDeviceInfo().getTimezone());
            item.put("locale", session.getDeviceInfo().getLocale());
            item.put("sdk_version", session.getDeviceInfo().getSdkVersion());
            item.put("events", new JSONArray(events));
        }
        catch (JSONException ex) {
            Log.d(LOGTAG, "Problem converting to JSON.", ex);
        }

        return item;
    }

    @Override
    public JSONObject buildItem(final String eventSource,
                                final String eventName,
                                final Map<String, String> params) {
        JSONObject item = new JSONObject();

        try {
            item.put("event_source", eventSource);
            item.put("event_name", eventName);
            item.put("event_timestamp", new Date().getTime());
            item.put("event_params", buildParams(params));
        }
        catch (JSONException ex) {
            Log.d(LOGTAG, "Problem converting to JSON.", ex);
        }

        return item;
    }

    private JSONObject buildParams(final Map<String, String> params) throws JSONException {
        JSONObject p = new JSONObject();

        for(String key : params.keySet()) {
            p.put(key, params.get(key));
        }

        return p;
    }
}
