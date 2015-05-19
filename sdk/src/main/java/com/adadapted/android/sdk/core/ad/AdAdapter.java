package com.adadapted.android.sdk.core.ad;

import org.json.JSONObject;

/**
 * Created by chrisweeden on 4/1/15.
 */
public interface AdAdapter {
    interface Listener {
        void onAdGetRequestCompleted(JSONObject adJson);
        void onAdGetRequestFailed();
    }

    void getAds(JSONObject json);

    void addListener(Listener listener);
    void removeListener(Listener listener);
    void notifyAdGetRequestCompleted(JSONObject adJson);
    void notifyAdGetRequestFailed();
}
