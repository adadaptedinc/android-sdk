package com.adadapted.android.sdk.core.intercept;

import com.adadapted.android.sdk.core.session.Session;

import java.util.Set;

public interface InterceptAdapter {
    void retrieve(final Session session, Callback callback);
    void sendEvents(final Session session, final Set<InterceptEvent> events);


    interface Callback {
        void onSuccess(Intercept intercept);
    }
}
