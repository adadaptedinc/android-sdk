package com.adadapted.android.sdk.core.event;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 9/16/16.
 */
public interface AppEventAdapter {
    void sendBatch(JSONObject eventPayload, AppEventAdapterListener listener);
}
