package com.adadapted.android.sdk.core.anomaly;

import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 9/23/16.
 */
public class AdAnomalyTracker {
    private final Set<JSONObject> mQueuedEvents;

    private final AnomalyAdapter adapter;
    private final AnomalyBuilder builder;

    public AdAnomalyTracker(final AnomalyAdapter adapter,
                            final AnomalyBuilder builder) {
        this.adapter = adapter;
        this.builder = builder;

        mQueuedEvents = new HashSet<>();
    }

    public void registerAnomaly(final Session session,
                                final String adId,
                                final String eventPath,
                                final String code,
                                final String message) {
        mQueuedEvents.add(builder.build(session, adId, eventPath, code, message));
    }

    public void publishAnomalies() {
        if(mQueuedEvents.isEmpty()) {
            return;
        }

        final Set<JSONObject> currentEvents = new HashSet<>(mQueuedEvents);
        mQueuedEvents.clear();

        final JSONArray eventsArray = new JSONArray(currentEvents);
        adapter.sendBatch(eventsArray);
    }
}
