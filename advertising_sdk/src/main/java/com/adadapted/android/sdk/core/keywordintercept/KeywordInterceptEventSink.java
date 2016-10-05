package com.adadapted.android.sdk.core.keywordintercept;

import org.json.JSONArray;

/**
 * Created by chrisweeden on 9/30/16.
 */

public interface KeywordInterceptEventSink {
    void sendBatch(JSONArray json);
}
