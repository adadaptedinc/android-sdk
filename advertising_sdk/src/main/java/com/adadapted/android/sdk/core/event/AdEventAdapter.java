package com.adadapted.android.sdk.core.event;

import org.json.JSONArray;

/**
 * Created by chrisweeden on 3/26/15.
 */
public interface AdEventAdapter {
    void sendBatch(JSONArray events, AdEventAdapterListener listener);
}
