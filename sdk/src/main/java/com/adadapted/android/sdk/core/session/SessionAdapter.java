package com.adadapted.android.sdk.core.session;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 3/26/15.
 */
public interface SessionAdapter {
    interface Listener {
        void onSessionInitRequestCompleted(JSONObject response);
        void onSessionInitRequestFailed();
        void onSessionReinitRequestNoContent();
        void onSessionReinitRequestFailed();
    }

    void sendInit(JSONObject request);
    void sendReinit(JSONObject request);

    void addListener(Listener listener);
    void removeListener(Listener listener);
}
