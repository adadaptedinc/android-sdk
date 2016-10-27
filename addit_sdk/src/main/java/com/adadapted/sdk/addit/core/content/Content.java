package com.adadapted.sdk.addit.core.content;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 9/26/16.
 */

public interface Content<PAYLOAD_T> {
    void acknowledge();
    void failed(String message);
    int getType();
    PAYLOAD_T getPayload();
}
