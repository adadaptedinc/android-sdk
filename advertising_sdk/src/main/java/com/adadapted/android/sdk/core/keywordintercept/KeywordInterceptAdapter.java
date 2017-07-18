package com.adadapted.android.sdk.core.keywordintercept;

import com.adadapted.android.sdk.core.session.Session;

import java.util.Set;

public interface KeywordInterceptAdapter {
    void init(final Session session, Callback callback);
    void sendBatch(final Set<KeywordInterceptEvent> events);


    interface Callback {
        void onSuccess(KeywordIntercept keywordIntercept);
    }
}
