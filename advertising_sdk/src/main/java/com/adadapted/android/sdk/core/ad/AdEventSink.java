package com.adadapted.android.sdk.core.ad;

import java.util.Set;

public interface AdEventSink {
    void sendBatch(Set<AdEvent> events);
}
