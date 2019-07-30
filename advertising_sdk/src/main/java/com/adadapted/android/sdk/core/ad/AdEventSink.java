package com.adadapted.android.sdk.core.ad;

import com.adadapted.android.sdk.core.session.Session;

import java.util.Set;

public interface AdEventSink {
    void sendBatch(final Session session, final Set<AdEvent> events);
}
