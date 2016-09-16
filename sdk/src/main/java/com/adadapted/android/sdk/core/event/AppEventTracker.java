package com.adadapted.android.sdk.core.event;

import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by chrisweeden on 9/16/16.
 */
public class AppEventTracker {
    private static final String LOGTAG = AppEventTracker.class.getName();

    private final AppEventAdapter adapter;
    private final AppEventBuilder builder;
    private final Set<JSONObject> events;

    private final AppEventAdapterListener mListener = new AppEventAdapterListener() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onFailure(JSONArray object) {

        }
    };

    public AppEventTracker(final AppEventAdapter adapter,
                           final AppEventBuilder builder) {
        this.adapter = adapter;
        this.builder = builder;

        this.events = new HashSet<>();
    }

    public void trackAppEvent(final String trackingId,
                              final String eventName,
                              final Map<String, String> params) {
        final JSONObject event = builder.buildItem(trackingId, eventName, params);
        events.add(event);
    }

    public void publishEvents(final Session session) {
        if(events.size() > 0) {
            final Set<JSONObject> currentEvents = new HashSet<>(events);
            events.clear();

            JSONObject eventPayload = builder.build(session, currentEvents);

            adapter.sendBatch(eventPayload, mListener);
        }
    }
}
