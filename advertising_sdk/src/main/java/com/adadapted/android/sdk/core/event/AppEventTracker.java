package com.adadapted.android.sdk.core.event;

import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by chrisweeden on 9/26/16.
 */
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

        final JSONObject json = builder.buildItem(eventWrapper, eventSource, eventName, eventParams);
        sink.publishEvent(json);
    }
}
