package com.adadapted.android.sdk.core.keywordintercept;

import com.adadapted.android.sdk.core.session.Session;

public interface KeywordInterceptAdapter {
    void init(final Session session, Callback callback);


    interface Callback {
        void onSuccess(KeywordIntercept keywordIntercept);
        void onFailure();
    }
}
