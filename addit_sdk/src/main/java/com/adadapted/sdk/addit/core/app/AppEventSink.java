package com.adadapted.sdk.addit.core.app;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 9/26/16.
 */

public interface AppEventSink {
    void publishEvent(JSONObject appEvent);
}
