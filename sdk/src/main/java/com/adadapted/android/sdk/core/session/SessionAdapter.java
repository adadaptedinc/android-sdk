package com.adadapted.android.sdk.core.session;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 3/26/15.
 */
public interface SessionAdapter {
    interface Listener {
        void onSessionRequestCompleted(JSONObject response);
    }

    void sendInit(JSONObject json);

    void addListener(Listener listener);
    void removeListener(Listener listener);
    void notifySessionRequestCompleted(JSONObject response);
}
