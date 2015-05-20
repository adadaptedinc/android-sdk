package com.adadapted.android.sdk.core.event;

import org.json.JSONArray;

/**
 * Created by chrisweeden on 3/26/15.
 */
public interface EventAdapter {
    interface Listener {
        void onEventsPublished();
        void onEventsPublishFailed(JSONArray json);

    }

    void sendBatch(JSONArray events);

    void addListener(Listener listener);
    void removeListener(Listener listener);
    void notifyEventsPublished();
    void notifyEventsFailed(JSONArray json);
}
