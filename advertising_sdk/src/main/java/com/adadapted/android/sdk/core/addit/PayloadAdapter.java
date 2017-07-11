package com.adadapted.android.sdk.core.addit;

import org.json.JSONObject;

import java.util.List;

public interface PayloadAdapter {
    interface Callback {
        void onSuccess(List<Content> content);
        void onFailure(String message);
    }

    void pickup(JSONObject request, Callback callback);
    void publishEvent(JSONObject payloadEvent);
}
