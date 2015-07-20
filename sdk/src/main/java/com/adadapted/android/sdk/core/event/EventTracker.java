package com.adadapted.android.sdk.core.event;

import android.util.Log;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.event.model.EventTypes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class EventTracker implements EventAdapter.Listener {
    private static final String TAG = EventTracker.class.getName();

    private static final int MAX_QUEUE_SIZE = 10;
    private static final int MAX_FAILED_RETRIES = 2;

    private final EventAdapter eventAdapter;
    private final EventRequestBuilder builder;

    private final Set<JSONObject> queuedEvents;
    private int failedRetries;

    public EventTracker(EventAdapter eventAdapter, EventRequestBuilder builder) {
        this.eventAdapter = eventAdapter;
        this.builder = builder;

        this.queuedEvents = new HashSet<>();
        failedRetries = 0;
    }

    public Set<JSONObject> getQueuedEvents() {
        return queuedEvents;
    }

    public void publishEvents() {
        if(queuedEvents.isEmpty()) {
            Log.d(TAG, "No items queued to publish.");
        }
        else {
            Set<JSONObject> currentEvents = new HashSet<>(getQueuedEvents());
            queuedEvents.clear();

            JSONArray eventsArray = new JSONArray(currentEvents);
            eventAdapter.sendBatch(eventsArray);
        }
    }

    private void sendBatchRetry(JSONArray json) {
        if(failedRetries <= MAX_FAILED_RETRIES) {
            eventAdapter.sendBatch(json);
        }
        else {
            Log.d(TAG, "Maximum failed retries. No longer sending batch retries.");
        }
    }

    public void trackImpressionBeginEvent(String sessionId, Ad ad) {
        trackEvent(sessionId, ad, EventTypes.IMPRESSION, "");
    }

    public void trackImpressionEndEvent(String sessionId, Ad ad) {
        trackEvent(sessionId, ad, EventTypes.IMPRESSION_END, "");
    }


    public void trackInteractionEvent(String sessionId, Ad ad) {
        trackEvent(sessionId, ad, EventTypes.INTERACTION, "");
    }

    public void trackPopupBeginEvent(String sessionId, Ad ad) {
        trackEvent(sessionId, ad, EventTypes.POPUP_BEGIN, "");
    }

    public void trackPopupEndEvent(String sessionId, Ad ad) {
        trackEvent(sessionId, ad, EventTypes.POPUP_END, "");
    }

    public void trackCustomEvent(String sessionId, Ad ad, String eventName) {
        trackEvent(sessionId, ad, EventTypes.CUSTOM, eventName);
    }

    private void trackEvent(String sessionId, Ad ad, EventTypes eventType, String eventName) {
        Log.d(TAG, "Queueing " + eventType + " for " + ad.getAdId());

        DeviceInfo deviceInfo = AdAdapted.getInstance().getDeviceInfo();
        queuedEvents.add(builder.build(deviceInfo, sessionId, ad, eventType, eventName));

        if(queuedEvents.size() >= MAX_QUEUE_SIZE) {
            publishEvents();
        }
    }

    @Override
    public void onEventsPublished() {
        failedRetries = 0;
    }

    @Override
    public void onEventsPublishFailed(JSONArray json) {
        failedRetries++;
        sendBatchRetry(json);
    }

    @Override
    public String toString() {
        return "EventTracker{" +
                "eventAdapter=" + eventAdapter +
                ", builder=" + builder +
                ", queuedEvents=" + queuedEvents +
                '}';
    }
}
