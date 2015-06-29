package com.adadapted.android.sdk.core.content;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 6/5/15.
 */
public class ContentPayload {
    public static final int ADD_TO_LIST = 0;
    public static final int RECIPE_FAVORITE = 1;

    private final int type;
    private final JSONObject payload;

    public ContentPayload(int type, JSONObject payload) {
        this.type = type;
        this.payload = payload;
    }

    public int getType() {
        return type;
    }

    public JSONObject getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "ContentPayload";
    }
}
