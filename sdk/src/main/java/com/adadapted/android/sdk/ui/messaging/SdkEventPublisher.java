package com.adadapted.android.sdk.ui.messaging;

import com.adadapted.android.sdk.core.event.model.AdEvent;
import com.adadapted.android.sdk.core.session.model.Session;

/**
 * Created by chrisweeden on 8/18/15.
 */
public class SdkEventPublisher {
    public static class EventTypes {
        public static final String IMPRESSION = "impression";
        public static final String CLICK = "click";
    }

    private AaSdkEventListener mListener;

    public SdkEventPublisher() {

    }

    public void setListener(final AaSdkEventListener listener) {
        mListener = listener;
    }

    public void unsetListener() {
        mListener = null;
    }

    public void publishHasAdsToServe(final Session session) {
        boolean enabled = (session != null) && session.hasActiveCampaigns();

        mListener.onHasAdsToServe(enabled);
    }

    public void publishAdEvent(final AdEvent event) {
        if(mListener == null) {
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
