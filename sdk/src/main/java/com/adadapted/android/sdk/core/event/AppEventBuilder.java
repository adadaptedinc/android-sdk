package com.adadapted.android.sdk.core.event;

import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONObject;

import java.util.Map;
import java.util.Set;

/**
 * Created by chrisweeden on 9/16/16.
 */
public interface AppEventBuilder {
    JSONObject build(Session session, Set<JSONObject> currentEvents);
    JSONObject buildItem(String trackingId, String eventName, Map<String, String> params);
}
