package com.adadapted.android.sdk.core.anomaly;

import org.json.JSONArray;

/**
 * Created by chrisweeden on 9/23/16.
 */

public interface AnomalyAdapter {
    void sendBatch(JSONArray events, AnomalyAdapterListener listener);
}
