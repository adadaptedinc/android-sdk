package com.adadapted.android.sdk.core.addit.payload;

import org.json.JSONObject;

public interface PayloadEventSink {
    void publishEvent(JSONObject payloadEvent);
}
