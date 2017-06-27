package com.adadapted.android.sdk.core.keywordintercept;

import com.adadapted.android.sdk.core.keywordintercept.model.KeywordIntercept;

import org.json.JSONObject;

public interface KeywordInterceptAdapter {
    void init(JSONObject request, Callback callback);


    interface Callback {
        void onSuccess(KeywordIntercept keywordIntercept);
        void onFailure();
    }
}
