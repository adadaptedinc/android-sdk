package com.adadapted.android.sdk.core.addit.payload;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by chrisweeden on 2/9/17.
 */

public interface PayloadAdapter {
    interface Callback {
        void onSuccess(List<PayloadContent> content);
        void onFailure(String message);
    }

    void pickup(JSONObject request, Callback callback);
}
