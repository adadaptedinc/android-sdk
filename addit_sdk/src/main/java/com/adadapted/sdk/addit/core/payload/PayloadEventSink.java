package com.adadapted.sdk.addit.core.payload;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 2/9/17.
 */

public interface PayloadEventSink {
    void publishEvent(JSONObject payloadEvent);
}
