package com.adadapted.android.sdk.core.event.model;

/**
 * Created by chrisweeden on 8/18/15.
 */
public class AdEvent {
    public static class Types {
        public static final String IMPRESSION = "impression";
        public static final String IMPRESSION_END = "impression_end";
        public static final String INTERACTION = "interaction";
        public static final String POPUP_BEGIN = "popup_begin";
        public static final String POPUP_END = "popup_end";
        public static final String CUSTOM = "custom";
    }

    private final String mEventType;
    private final String mZoneId;

    public AdEvent(String mEventType, String mZoneId) {
        this.mEventType = mEventType;
        this.mZoneId = mZoneId;
    }

    public String getEventType() {
        return mEventType;
    }

    public String getZoneId() {
        return mZoneId;
    }
}
