package com.adadapted.android.sdk.core.ad;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 4/1/15.
 */
public interface AdAdapter {
    void getAds(JSONObject json, AdAdapterListener listener);
}
