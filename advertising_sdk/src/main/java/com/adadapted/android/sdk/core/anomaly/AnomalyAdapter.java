package com.adadapted.android.sdk.core.anomaly;

import org.json.JSONArray;

public interface AnomalyAdapter {
    void sendBatch(JSONArray events);
}
