package com.adadapted.android.sdk;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 3/23/15.
 */
class EventTracker {
    private static final String TAG = EventTracker.class.getName();

    private final EventAdapter eventAdapter;
    private final EventRequestBuilder builder;

    private Set<JSONObject> queuedEvents;

    EventTracker(EventAdapter eventAdapter) {
        this.eventAdapter = eventAdapter;

        this.queuedEvents = new HashSet<>();
        this.builder = new EventRequestBuilder();
    }

    public Set<JSONObject> getQueuedEvents() {
        return queuedEvents;
    }

    private void trackEvent(String sessionId, Ad ad, EventType eventType, String eventName) {
        Log.d(TAG, "Queueing " + eventType + " for " + ad.getAdId());

        DeviceInfo deviceInfo = AdAdapted.getInstance().getDeviceInfo();
        queuedEvents.add(builder.build(deviceInfo, sessionId, ad, eventType, eventName));

        if(queuedEvents.size() > 5) {
            publishEvents();
        }
    }

    void publishEvents() {
        if(queuedEvents.size() > 0) {
            Set<JSONObject> currentEvents = new HashSet<>(queuedEvents);
            queuedEvents.clear();

            JSONArray eventsArray = new JSONArray(currentEvents);
            eventAdapter.sendBatch(eventsArray);
        }
        else {
            Log.d(TAG, "No items queued to publish.");
        }
    }

    void trackImpressionBeginEvent(String sessionId, Ad ad) {
        trackEvent(sessionId, ad, EventType.IMPRESSION, "");
    }

    void trackImpressionEndEvent(String sessionId, Ad ad) {
        trackEvent(sessionId, ad, EventType.IMPRESSION_END, "");
    }


    void trackInteractionEvent(String sessionId, Ad ad) {
        trackEvent(sessionId, ad, EventType.INTERACTION, "");
    }

    void trackPopupBeginEvent(String sessionId, Ad ad) {
        trackEvent(sessionId, ad, EventType.POPUP_BEGIN, "");
    }

    void trackPopupEndEvent(String sessionId, Ad ad) {
        trackEvent(sessionId, ad, EventType.POPUP_END, "");
    }

    void trackCustomEvent(String sessionId, Ad ad, String eventName) {
        trackEvent(sessionId, ad, EventType.IMPRESSION, eventName);
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
