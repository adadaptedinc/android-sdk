package com.adadapted.android.sdk.core.ad;

import org.json.JSONArray;

/**
 * Created by chrisweeden on 9/29/16.
 */

public interface AdEventSink {
    void sendBatch(JSONArray jsonArray);
}
