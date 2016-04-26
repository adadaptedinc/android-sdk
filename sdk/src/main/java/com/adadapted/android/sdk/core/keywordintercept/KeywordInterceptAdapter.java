package com.adadapted.android.sdk.core.keywordintercept;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by chrisweeden on 6/23/15.
 */
public interface KeywordInterceptAdapter {
    void init(JSONObject request, KeywordInterceptInitListener listener);
    void track(JSONArray request, KeywordInterceptTrackListener listener);
}
