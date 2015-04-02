package com.adadapted.android.sdk;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 4/1/15.
 */
interface AdAdapter {
    interface Listener {
        void onAdGetRequestCompleted(JSONObject adJson);
    }

    void getAds(JSONObject json);

    void addListener(Listener listener);
    void removeListener(Listener listener);
    void notifyAdGetRequestCompleted(JSONObject adJson);
}
