package com.adadapted.android.sdk.core.ad;

import org.json.JSONArray;

public interface AdEventSink {
    void sendBatch(JSONArray jsonArray);
}
