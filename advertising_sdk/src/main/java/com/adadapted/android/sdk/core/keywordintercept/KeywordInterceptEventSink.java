package com.adadapted.android.sdk.core.keywordintercept;

import java.util.Set;

public interface KeywordInterceptEventSink {
    void sendBatch(final Set<KeywordInterceptEvent> events);
}
