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

    public ViewAdWrapper(Session session, Ad ad) {
        if(ad != null) {
            DeviceInfo deviceInfo = DeviceInfoFactory.getsDeviceInfo();
            mEventTracker = EventTrackerFactory.createEventTracker(deviceInfo);
        }

        mSession = session;
        mAd = ad;
    }

    public static ViewAdWrapper createEmptyCurrentAd(Session session) {
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

    public void markAdAsHidden() {
        mAd.hideAd();
    }

    public boolean isHiddenOnInteraction() {
        return hasAd() && mAd.isHiddenAfterInteraction();
    }

    public Session getSession() {
        return mSession;
    }

    public void beginAdTracking() {
        if(hasAd() && !trackingHasStarted) {
            mEventTracker.trackImpressionBeginEvent(mSession, getAd());

            AdEvent adEvent = new AdEvent(AdEvent.Types.IMPRESSION, getAd().getZoneId());
            SdkEventPublisherFactory.getSdkEventPublisher().publishAdEvent(adEvent);

            trackingHasStarted = true;
        }
    }

    public void completeAdTracking() {
        if(hasAd() && trackingHasStarted) {
            mEventTracker.trackImpressionEndEvent(mSession, getAd());
            trackingHasStarted = false;
        }
    }

    public void trackInteraction() {
        if(hasAd() && trackingHasStarted) {
            mEventTracker.trackInteractionEvent(mSession, getAd());

            AdEvent adEvent = new AdEvent(AdEvent.Types.INTERACTION, getAd().getZoneId());
            SdkEventPublisherFactory.getSdkEventPublisher().publishAdEvent(adEvent);

            if(mAd.isHiddenAfterInteraction()) {
                markAdAsHidden();
            }
        }
    }

    public void trackPayloadDelivered() {
        if(hasAd() && trackingHasStarted) {
            mEventTracker.trackCustomEvent(mSession, getAd(), "payload_delivered");
        }
    }
}
