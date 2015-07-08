package com.adadapted.android.sdk.ui.model;

import android.content.Context;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.AdTypes;
import com.adadapted.android.sdk.core.event.EventTracker;
import com.adadapted.android.sdk.ext.factory.EventTrackerFactory;

/**
 * Created by chrisweeden on 5/26/15.
 */
public class ViewAd {
    private static final String TAG = ViewAd.class.getName();

    private final EventTracker eventTracker;
    private final String sessionId;
    private final Ad ad;

    private boolean trackingHasStarted = false;

    public ViewAd(Context context, String sessionId, Ad ad) {
        eventTracker = EventTrackerFactory.getInstance(context).createEventTracker();

        this.sessionId = sessionId;
        this.ad = ad;
    }

    public static ViewAd createEmptyCurrentAd(Context context, String sessionId) {
        return new ViewAd(context, sessionId, null);
    }

    public Ad getAd() {
        return ad;
    }

    public boolean hasAd() {
        return (ad != null);
    }

    public AdTypes getAdType() {
        if(hasAd()) {
            return ad.getAdType().getType();
        }

        return AdTypes.NULL;
    }

    public boolean isHiddenOnInteraction() {
        return hasAd() && ad.isHiddenAfterInteraction();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void beginAdTracking() {
        if(hasAd()) {
            eventTracker.trackImpressionBeginEvent(sessionId, getAd());
            trackingHasStarted = true;
        }
    }

    public void completeAdTracking() {
        if(hasAd() && trackingHasStarted) {
            eventTracker.trackImpressionEndEvent(sessionId, getAd());
            trackingHasStarted = false;
        }
    }

    public void trackInteraction() {
        if(hasAd() && trackingHasStarted) {
            eventTracker.trackInteractionEvent(sessionId, getAd());

            if(ad.isHiddenAfterInteraction()) {
                ad.hideAd();
            }
        }
    }

    public void trackPayloadDelivered() {
        if(hasAd() && trackingHasStarted) {
            eventTracker.trackCustomEvent(sessionId, getAd(), "payload_delivered");
        }
    }

    public void flush() {
        eventTracker.publishEvents();
    }
}
