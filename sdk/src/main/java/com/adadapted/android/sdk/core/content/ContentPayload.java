package com.adadapted.android.sdk.core.content;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 6/5/15.
 */
public class ContentPayload {
    private final String type;
    private final JSONObject payload;

    public ContentPayload(String type, JSONObject payload) {
        this.type = type;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public JSONObject getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "ContentPayload{" +
                "type='" + type + '\'' +
                ", payload=" + payload +
                '}';
    }
}
