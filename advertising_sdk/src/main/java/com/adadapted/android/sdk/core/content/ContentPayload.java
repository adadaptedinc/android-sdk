package com.adadapted.android.sdk.core.content;

import org.json.JSONObject;

public interface ContentPayload {
    void acknowledge();
    int getType();
    JSONObject getPayload();
}
