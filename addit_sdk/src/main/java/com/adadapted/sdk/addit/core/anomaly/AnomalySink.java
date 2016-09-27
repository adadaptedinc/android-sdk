package com.adadapted.sdk.addit.core.anomaly;

import org.json.JSONArray;

/**
 * Created by chrisweeden on 9/26/16.
 */
public interface AnomalySink {
    void publishAnomaly(JSONArray json);
}
