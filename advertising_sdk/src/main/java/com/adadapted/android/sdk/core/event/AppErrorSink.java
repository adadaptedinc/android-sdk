package com.adadapted.android.sdk.core.event;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 9/29/16.
 */

public interface AppErrorSink {
    void publishError(JSONObject appEvent);
}
