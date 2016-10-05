package com.adadapted.android.sdk.ui.messaging;

import com.adadapted.android.sdk.core.event.model.AdEvent;
import com.adadapted.android.sdk.ext.management.AdEventTrackingManager;

/**
 * Created by chrisweeden on 8/18/15.
 */
public class SdkEventPublisher implements AdEventTrackingManager.Callback {
    private static final String LOGTAG = SdkEventPublisher.class.getName();

    private static SdkEventPublisher sPublisherManager;

    public static SdkEventPublisher getInstance() {
        if(sPublisherManager == null) {
            sPublisherManager = new SdkEventPublisher();
        }

        return sPublisherManager;
    }

    private static class EventTypes {
        static final String IMPRESSION = "impression";
        static final String CLICK = "click";
    }

    private AaSdkEventListener mListener;

    private SdkEventPublisher() {
        AdEventTrackingManager.addCallback(this);
    }

    @Override
    public void onAdEventTracked(final AdEvent event) {
        publishAdEvent(event);
    }

    public void setListener(final AaSdkEventListener listener) {
        mListener = listener;
    }

    public void unsetListener() {
        mListener = null;
    }

    private void publishAdEvent(final AdEvent event) {
        if(mListener == null || event == null) {
            return;
        }

        if(event.getEventType().equals(AdEvent.Types.IMPRESSION)) {
            mListener.onNextAdEvent(event.getZoneId(), EventTypes.IMPRESSION);
        }
        else if(event.getEventType().equals(AdEvent.Types.INTERACTION)) {
            mListener.onNextAdEvent(event.getZoneId(), EventTypes.CLICK);
        }
    }
}
