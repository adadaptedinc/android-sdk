package com.adadapted.android.sdk.core.keywordintercept;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by chrisweeden on 6/23/15.
 */
public interface KeywordInterceptAdapter {
    interface Listener {
        void onInitSuccess(JSONObject response);
        void onInitFailed();
        void onTrackSuccess();
        void onTrackFailed();
    }

    void init(JSONObject request);
    void track(JSONArray request);

    void addListener(Listener listener);
    void removeListener(Listener listener);
}
