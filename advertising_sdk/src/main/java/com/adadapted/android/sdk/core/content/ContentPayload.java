package com.adadapted.android.sdk.core.content;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 9/16/16.
 */
public interface ContentPayload {
    void acknowledge();
    int getType();
    JSONObject getPayload();
}
