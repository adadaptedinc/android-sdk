package com.adadapted.android.sdk;

import org.json.JSONArray;

/**
 * Created by chrisweeden on 3/26/15.
 */
interface EventAdapter {
    void sendBatch(JSONArray events) throws SdkNotInitializedException;

    interface Listener {
        void onEventsPublished();
    }

    void addListener(Listener listener);
    void removeListener(Listener listener);
    void notifyEventsPublished();
}
