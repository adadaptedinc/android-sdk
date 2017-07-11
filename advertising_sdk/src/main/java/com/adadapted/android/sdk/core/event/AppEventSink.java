package com.adadapted.android.sdk.core.event;

import java.util.Map;

public interface AppEventSink {
    void publishEvent(final String type,
                      final String name,
                      final Map<String, String> params);

    void publishError(final String code,
                      final String message,
                      final Map<String, String> params);
}
