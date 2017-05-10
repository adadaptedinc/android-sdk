package com.adadapted.android.sdk.core.keywordintercept;

import com.adadapted.android.sdk.core.keywordintercept.model.KeywordInterceptEvent;
import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONArray;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 9/30/16.
 */
public class KeywordInterceptEventTracker {
    private final KeywordInterceptEventSink sink;
    private final KeywordInterceptEventBuilder builder;
    private final Set<KeywordInterceptEvent> keywordInterceptEvents;

    public KeywordInterceptEventTracker(final KeywordInterceptEventSink sink,
                                        final KeywordInterceptEventBuilder builder) {
        this.sink = sink;
        this.builder = builder;

        keywordInterceptEvents = new HashSet<>();
    }

    public synchronized void trackEvent(final Session session,
                            final String searchId,
                            final String term,
                            final String userInput,
                            final String eventType) {
        final String appId = session.getDeviceInfo().getAppId();
        final String sessionId = session.getSessionId();
        final String udid = session.getDeviceInfo().getUdid();
        final String sdkVersion = session.getDeviceInfo().getSdkVersion();

        final KeywordInterceptEvent event = new KeywordInterceptEvent(appId, sessionId, udid, searchId,
                eventType, userInput, term, sdkVersion);
        fileEvent(event);
    }

    private void fileEvent(final KeywordInterceptEvent event) {
        final Set<KeywordInterceptEvent> events = new HashSet<>(keywordInterceptEvents);

        for(final KeywordInterceptEvent e : keywordInterceptEvents) {
            if(event.supercedes(e)) {
                events.remove(e);
            }
        }

        events.add(event);

        keywordInterceptEvents.clear();
        keywordInterceptEvents.addAll(events);
    }

    public void publishEvents() {
        if(keywordInterceptEvents.isEmpty()) {
            return;
        }

        final Set<KeywordInterceptEvent> events = new HashSet<>(keywordInterceptEvents);
        keywordInterceptEvents.clear();

        final JSONArray json = builder.buildEvents(events);
        sink.sendBatch(json);
    }
}
