package com.adadapted.android.sdk.core.event;

import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONObject;

import java.util.Map;

public interface AppEventBuilder {
    JSONObject buildWrapper(Session session);
    JSONObject buildItem(JSONObject eventWrapper, String eventSource, String eventName, Map<String, String> params);
}
