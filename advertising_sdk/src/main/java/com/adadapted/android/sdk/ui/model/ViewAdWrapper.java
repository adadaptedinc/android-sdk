package com.adadapted.android.sdk.ui.model;

import android.webkit.WebView;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.AdType;
import com.adadapted.android.sdk.core.ad.model.CustomAdEvents;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.ext.management.AdEventTrackingManager;

/**
 * Created by chrisweeden on 5/26/15.
 */
public class ViewAdWrapper {
    private static final String LOGTAG = ViewAdWrapper.class.getName();

    private final Session mSession;
    private final Ad mAd;

    private boolean trackingHasStarted = false;
    private boolean hasTrackedImpression = false;

    public ViewAdWrapper(final Session session, final Ad ad) {
        mSession = session;
        mAd = ad;
    }

    public static ViewAdWrapper createEmptyCurrentAd(final Session session) {
        return new ViewAdWrapper(session, null);
    }

    public Ad getAd() {
        return mAd;
    }

    public String getAdId() {
        if(hasAd()) {
            return getAd().getAdId();
        }

        return null;
    }

    public boolean hasAd() {
        return (mAd != null);
    }

    public String getAdType() {
        if(hasAd()) {
            return mAd.getAdType().getType();
        }

        return AdType.NULL;
    }

    private boolean trackingHasStarted() {
        return trackingHasStarted;
    }

    private boolean impressionHasBeenTracked() {
        return hasTrackedImpression;
    }

    private boolean shouldBeginTracking() {
        return !trackingHasStarted() && !impressionHasBeenTracked();
    }

    private boolean trackingHasBegun() {
        return trackingHasStarted();
    }

    public void markAdAsHidden() {
        if(hasAd()) {
            mAd.hideAd();
        }
    }

    public boolean isHiddenOnInteraction() {
        return hasAd() && mAd.isHiddenAfterInteraction();
    }

    public Session getSession() {
        return mSession;
    }

    public void beginAdTracking(final WebView trackingWebView) {
        if(hasAd() && shouldBeginTracking()) {
            AdEventTrackingManager.trackImpressionBeginEvent(mSession, getAd());
            trackingWebView.loadData(getAd().getTrackingHtml(), "text/html", null);

            trackingHasStarted = true;
            hasTrackedImpression = true;
        }
    }

    public void completeAdTracking() {
        if(hasAd() && trackingHasBegun()) {
            AdEventTrackingManager.trackImpressionEndEvent(mSession, getAd());
            trackingHasStarted = false;
        }
    }

    public void trackInteraction() {
        if(hasAd() && trackingHasBegun()) {
            AdEventTrackingManager.trackInteractionEvent(mSession, getAd());

            if(mAd.isHiddenAfterInteraction()) {
                markAdAsHidden();
            }
        }
    }

    public void trackPayloadDelivered() {
        if(hasAd() && trackingHasBegun()) {
            AdEventTrackingManager.trackCustomEvent(
                    mSession,
                    getAd(),
                    CustomAdEvents.EVENTNAME_PAYLOAD_DELIVERED);
        }
    }
}
