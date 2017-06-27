package com.adadapted.android.sdk.core.event;

import org.json.JSONObject;

public interface AppErrorSink {
    void publishError(JSONObject appEvent);
}
