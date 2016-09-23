package com.adadapted.android.sdk.core.anomaly;

import com.adadapted.android.sdk.core.session.model.Session;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 9/23/16.
 */
public class AnomalyTracker {
    private final Set<JSONObject> mQueuedEvents;

    private final AnomalyAdapter adapter;
    private final AnomalyBuilder builder;

    private final AnomalyAdapterListener mAnomalyListener = new AnomalyAdapterListener() {
        @Override
        public void onSuccess() {}

        @Override
        public void onFailure(final JSONArray json) {}
    };

    public AnomalyTracker(final AnomalyAdapter adapter,
                          final AnomalyBuilder builder) {
        this.adapter = adapter;
        this.builder = builder;

        mQueuedEvents = new HashSet<>();
    }

    public Set<JSONObject> getQueuedEvents() {
        return mQueuedEvents;
    }

    public void registerAnomaly(final Session session,
                                final String adId,
                                final String eventPath,
                                final String code,
                                final String message) {
        mQueuedEvents.add(builder.build(session, adId, eventPath, code, message));
    }

    public void publishEvents() {
        if(!mQueuedEvents.isEmpty()) {
            final Set<JSONObject> currentEvents = new HashSet<>(getQueuedEvents());
            mQueuedEvents.clear();

            final JSONArray eventsArray = new JSONArray(currentEvents);
            adapter.sendBatch(eventsArray, mAnomalyListener);
        }
    }
}
