package com.adadapted.android.sdk.ui.messaging;

import com.adadapted.android.sdk.core.event.model.AdEvent;

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

    public void publishAdEvent(final AdEvent event) {
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
