package com.adadapted.android.sdk;

import android.util.Log;

import org.json.JSONArray;

/**
 * Created by chrisweeden on 3/23/15.
 */
class EventTracker {
    private static final String TAG = EventTracker.class.getName();

    private final EventAdapter eventAdapter;
    private final EventRequestBuilder builder;

    private JSONArray queuedEvents;

    EventTracker(EventAdapter eventAdapter) {
        this.eventAdapter = eventAdapter;

        this.queuedEvents = new JSONArray();
        this.builder = new EventRequestBuilder();
    }

    public JSONArray getQueuedEvents() {
        return queuedEvents;
    }

    private void trackEvent(String sessionId, Ad ad, EventType eventType, String eventName) {
        Log.d(TAG, "Queueing " + eventType + " for " + ad.getAdId());

        DeviceInfo deviceInfo = AdAdapted.getInstance().getDeviceInfo();
        queuedEvents.put(builder.build(deviceInfo, sessionId, ad, eventType, eventName));

        if(queuedEvents.length() > 5) {
            publishEvents();
        }
    }

    void publishEvents() {
        if(queuedEvents.length() > 0) {
            eventAdapter.sendBatch(queuedEvents);
            queuedEvents = new JSONArray();
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
