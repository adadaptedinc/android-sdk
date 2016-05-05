package com.adadapted.android.sdk.ui.model;

import com.adadapted.android.sdk.core.ad.model.Ad;
import com.adadapted.android.sdk.core.ad.model.AdTypes;
import com.adadapted.android.sdk.core.device.model.DeviceInfo;
import com.adadapted.android.sdk.core.event.EventTracker;
import com.adadapted.android.sdk.core.event.model.AdEvent;
import com.adadapted.android.sdk.core.session.model.Session;
import com.adadapted.android.sdk.ext.factory.DeviceInfoFactory;
import com.adadapted.android.sdk.ext.factory.EventTrackerFactory;
import com.adadapted.android.sdk.ui.messaging.SdkEventPublisherFactory;

/**
 * Created by chrisweeden on 5/26/15.
 */
public class ViewAdWrapper {
    private static final String LOGTAG = ViewAdWrapper.class.getName();

    private EventTracker mEventTracker;
    private final Session mSession;
    private final Ad mAd;

    private boolean trackingHasStarted = false;
    private boolean hasTrackedImpression = false;

    public ViewAdWrapper(final Session session, final Ad ad) {
        if(ad != null) {
            DeviceInfo deviceInfo = DeviceInfoFactory.getsDeviceInfo();
            mEventTracker = EventTrackerFactory.createEventTracker(deviceInfo);
        }

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

    public AdTypes getAdType() {
        if(hasAd()) {
            return mAd.getAdType().getType();
        }

        return AdTypes.NULL;
    }

    private boolean trackingHasStarted() {
        return trackingHasStarted;
    }

    private boolean impressionHasBeenTracked() {
        return hasTrackedImpression;
    }

    private boolean hasEventTracker() {
        return mEventTracker != null;
    }

    private boolean shouldBeginTracking() {
        return !trackingHasStarted() && !impressionHasBeenTracked() && hasEventTracker();
    }

    private boolean trackingHasBegun() {
        return trackingHasStarted() && hasEventTracker();
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

    public void beginAdTracking() {
        if(hasAd() && shouldBeginTracking()) {
            mEventTracker.trackImpressionBeginEvent(mSession, getAd());

            AdEvent adEvent = new AdEvent(AdEvent.Types.IMPRESSION, getAd().getZoneId());
            SdkEventPublisherFactory.getSdkEventPublisher().publishAdEvent(adEvent);

            trackingHasStarted = true;
            hasTrackedImpression = true;
        }
    }

    public void completeAdTracking() {
        if(hasAd() && trackingHasBegun()) {
            mEventTracker.trackImpressionEndEvent(mSession, getAd());
            trackingHasStarted = false;
        }
    }

    public void trackInteraction() {
        if(hasAd() && trackingHasBegun()) {
            mEventTracker.trackInteractionEvent(mSession, getAd());

            AdEvent adEvent = new AdEvent(AdEvent.Types.INTERACTION, getAd().getZoneId());
            SdkEventPublisherFactory.getSdkEventPublisher().publishAdEvent(adEvent);

            if(mAd.isHiddenAfterInteraction()) {
                markAdAsHidden();
            }
        }
    }

    public void trackPayloadDelivered() {
        if(hasAd() && trackingHasBegun()) {
            mEventTracker.trackCustomEvent(mSession, getAd(), EventTracker.EVENTNAME_PAYLOAD_DELIVERED);
        }
    }
}
