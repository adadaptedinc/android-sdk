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
    private final Context context;
    private final String sessionId;
    private final Ad ad;

    private boolean trackingHasStarted = false;
    private boolean isStoppingForPopup = false;

    public ViewAd(Context context, String sessionId, Ad ad) {
        this.context = context;
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
        return ad.getAdType().getType();
    }

    public boolean isStoppingForPopup() {
        return isStoppingForPopup;
    }

    public void beginAdTracking() {
        getEventTracker().trackImpressionBeginEvent(sessionId, getAd());
        trackingHasStarted = true;
    }

    public void completeAdTracking() {
        if(trackingHasStarted) {
            getEventTracker().trackImpressionEndEvent(sessionId, getAd());
            trackingHasStarted = false;
        }
    }

    public void trackInteraction() {
        getEventTracker().trackInteractionEvent(sessionId, getAd());
        trackPopupBegin();

        isStoppingForPopup = true;
    }

    public void trackPopupBegin() {
        getEventTracker().trackPopupBeginEvent(sessionId, getAd());
    }

    public void trackPopupEnd() {
        if(isStoppingForPopup()) {
            getEventTracker().trackPopupEndEvent(sessionId, getAd());
            isStoppingForPopup = false;
        }
    }

    public void flush() {
        getEventTracker().publishEvents();
    }

    private EventTracker getEventTracker() {
        return EventTrackerFactory.getInstance(context).createEventTracker();
    }
}
