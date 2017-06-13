package com.adadapted.android.sdk.core.ad;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 9/29/16.
 */
public class AdEventTracker {
    private final AdEventSink sink;
    private final AdEventRequestBuilder builder;
    private final Set<JSONObject> currentEvents;

    public AdEventTracker(final AdEventSink sink,
                          final AdEventRequestBuilder builder) {
        this.sink = sink;
        this.builder = builder;

        this.currentEvents = new HashSet<>();
    }

    public void trackEvent(final Session session,
                           final Ad ad,
                           final String eventType,
                           final String eventName) {
        final JSONObject adEvent = builder.build(session, ad, eventType, eventName);
        if(adEvent != null) {
            currentEvents.add(adEvent);
        }
    }

    public void publishEvents() {
        if(currentEvents.isEmpty()) {
            return;
        }

        final Set<JSONObject> pendingEvents = new HashSet<>(currentEvents);
        currentEvents.clear();

        sink.sendBatch(new JSONArray(pendingEvents));
    }
}
