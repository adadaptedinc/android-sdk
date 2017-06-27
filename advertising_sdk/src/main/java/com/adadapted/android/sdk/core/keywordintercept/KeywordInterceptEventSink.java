package com.adadapted.android.sdk.core.keywordintercept;

import org.json.JSONArray;

public interface KeywordInterceptEventSink {
    void sendBatch(JSONArray json);
}
