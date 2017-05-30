package com.adadapted.android.sdk.core.addit.payload;

import com.adadapted.android.sdk.core.addit.Content;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by chrisweeden on 2/9/17.
 */

public interface PayloadAdapter {
    interface Callback {
        void onSuccess(List<Content> content);
        void onFailure(String message);
    }

    void pickup(JSONObject request, Callback callback);
}
