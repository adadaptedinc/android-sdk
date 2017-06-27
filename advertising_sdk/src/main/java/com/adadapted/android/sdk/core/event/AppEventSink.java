package com.adadapted.android.sdk.core.event;

import org.json.JSONObject;

public interface AppEventSink {
    void publishEvent(JSONObject appEvent);
}
