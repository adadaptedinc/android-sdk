package com.adadapted.android.sdk.core.ad.model;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 4/15/15.
 */
public class JsonAdType extends AdType {
    private JSONObject json;

    public JsonAdType() {
        setType(AdTypes.JSON);
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }
}
