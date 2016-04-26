package com.adadapted.android.sdk.core.event;

import org.json.JSONArray;

/**
 * Created by chrisweeden on 3/26/15.
 */
public interface EventAdapter {
    void sendBatch(JSONArray events, EventAdapterListener listener);
}
