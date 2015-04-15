package com.adadapted.android.sdk.core.event;

import android.util.Log;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.core.ad.Ad;
import com.adadapted.android.sdk.core.device.DeviceInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chrisweeden on 3/23/15.
 */
public class EventTracker {
    private static final String TAG = EventTracker.class.getName();

    private static final int EMPTY_QUEUE_SIZE = 0;
    private static final int MAX_QUEUE_SIZE = 10;

    private final EventAdapter eventAdapter;
    private final EventRequestBuilder builder;

    private Set<JSONObject> queuedEvents;

    public EventTracker(EventAdapter eventAdapter) {
        this.eventAdapter = eventAdapter;

        this.queuedEvents = new HashSet<>();
        this.builder = new EventRequestBuilder();
    }

    public Set<JSONObject> getQueuedEvents() {
        return queuedEvents;
    }

    private void trackEvent(String sessionId, Ad ad, EventTypes eventType, String eventName) {
        Log.d(TAG, "Queueing " + eventType + " for " + ad.getAdId());

        DeviceInfo deviceInfo = AdAdapted.getInstance().getDeviceInfo();
        queuedEvents.add(builder.build(deviceInfo, sessionId, ad, eventType, eventName));

        if(queuedEvents.size() >= MAX_QUEUE_SIZE) {
            publishEvents();
        }
    }

    public void publishEvents() {
        if(queuedEvents.size() > EMPTY_QUEUE_SIZE) {
            Set<JSONObject> currentEvents = new HashSet<>(queuedEvents);
            queuedEvents.clear();

            JSONArray eventsArray = new JSONArray(currentEvents);
            eventAdapter.sendBatch(eventsArray);
        }
        else {
            Log.d(TAG, "No items queued to publish.");
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
        trackEvent(sessionId, ad, EventTypes.IMPRESSION, eventName);
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
