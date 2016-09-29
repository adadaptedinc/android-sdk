package com.adadapted.sdk.addit.core.app;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 9/29/16.
 */

public interface AppErrorSink {
    void publishError(JSONObject appEvent);
}
