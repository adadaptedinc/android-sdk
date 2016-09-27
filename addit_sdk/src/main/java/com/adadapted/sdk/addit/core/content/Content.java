package com.adadapted.sdk.addit.core.content;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 9/26/16.
 */

public interface Content {
    void acknowledge();
    int getType();
    JSONObject getPayload();
}
