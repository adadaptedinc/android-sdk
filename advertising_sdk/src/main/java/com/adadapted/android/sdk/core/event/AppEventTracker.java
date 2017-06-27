package com.adadapted.android.sdk.core.event;

import android.util.Log;

import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONObject;

import java.util.Map;

public class AppEventTracker {
    private static final String LOGTAG = AppEventTracker.class.getName();

    private final JSONObject eventWrapper;
    private final AppEventSink sink;
    private final AppEventBuilder builder;

    public AppEventTracker(final Session session,
                           final AppEventSink sink,
                           final AppEventBuilder builder) {
        this.sink = sink;
        this.builder = builder;
        this.eventWrapper = builder.buildWrapper(session);
    }

    public void trackEvent(final String eventSource,
                           final String eventName,
                           final Map<String, String> eventParams) {
        Log.i(LOGTAG, "Tracking Event: " + eventName);

        final JSONObject json = builder.buildItem(eventWrapper, eventSource, eventName, eventParams);
        sink.publishEvent(json);
    }
}
